package model.concrete;

import model.Model;
import model.network.GameClientHandler;
import view.GamePage;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameState{
     public Tile.Bag bag;
     List<Player> playersList;
     public Board board;
     private boolean isGameOver;
    private  static class GameStateHolder{ public static final GameState gm = new GameState();}
    public static GameState getGM() {return GameStateHolder.gm;}


    //CTOR
    public  GameState() {
      board = Board.getBoard();
       bag = Tile.Bag.getBag();
        playersList = new ArrayList<>();
      isGameOver = false;
    }

    //Getters
    public Board getBoard()
    {
        return this.board;
    }
    public Tile.Bag getBag()
    {
        return this.bag;
    }
    public  List<Player> getPlayersList() {
        return playersList;
    }

    public  boolean getIsGameOver(){return isGameOver;}


    // Functions
    public  void setTurns(){
        //extracting randomly tile for each player, setting is id, returning to bag
        int id = 1;

        System.out.println("inside set turns");
        for(Player p : playersList){
            Tile tempTile = bag.getRand();
            p.id = tempTile.score;
            bag.put(tempTile);
        }
        // sorting the list from big id to small id with sorting & reversing the order
        playersList = playersList.stream().sorted(Comparator.comparingInt(Player::getId).reversed())
                .collect(Collectors.toList());

        for(Player p : playersList)
        {
            p.id = id;
            id++;
        }
        //first player at list is now playing first randomly
    }

    public void initHands(){
        for(int i = 0; i < playersList.size(); i++){
            Player tmpPlayer = playersList.get(i);
            for(int j=0;j<playersList.get(i).handSize;j++) {
                tmpPlayer.playerHand.add(bag.getRand());
            }
            //Sending to update the Init pack for each player, using Player method to convert tiles to strings
            if(tmpPlayer.getClass().equals(HostPlayer.class)){
                Model.getModel().updatePlayerVals(0,tmpPlayer.convertTilesToStrings(tmpPlayer.playerHand));
            }
            else{
//                for (GameClientHandler client : clients) {
//                    client.sendMessage(message);
//                }
            }
        }
    }
    public  void addPlayer(Player player)
    {
        playersList.add(player);
    }

    public  Player isWinner(){
        int max = 0;
        Player tmpPlayer = null;
        //Winner: when the tiles bag is empty and the winner finished his pack
        if(bag.getTilesCounter() == 0){
            for(Player p : playersList){
                if(max < p.sumScore && p.handSize == 0){
                    max = p.sumScore;
                    tmpPlayer =  p;
                }
            }
            isGameOver = true;
            return tmpPlayer;
        }
        return null;
    }

    public String getTextFiles(){
        String folderPath = "src\\main\\resources\\search_folder";
        StringBuilder textFilesBuilder = new StringBuilder();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if(files != null){
            for(File file: files){
                textFilesBuilder.append(file.getName());
                textFilesBuilder.append(',');
            }
        }
        return textFilesBuilder.toString();
    }

    // converting string to Tiles[] for creating new Word
    public Word convertStrToWord(String strQuery,Player p){
        if(strQuery.equals("")){System.out.println("msg is null!!");return null;}
        //EXAMPLE: "CAR,5,6,False"
        String[] res = strQuery.split(",");
        String word = res[0];
        int row = Integer.parseInt(res[1]);
        int col = Integer.parseInt(res[2]);
        boolean vert = Boolean.parseBoolean(res[3]);

        //after parsing the strings , creating new Word
        Tile[] wordTile = getTileArr(word.toUpperCase(),p);
        Word tmpQuery = new Word(wordTile, row, col, vert);
        System.out.println("after convert str to word");
        return tmpQuery;
    }

    public  Tile[] getTileArr(String str, Player p) {
        Tile[] tileArr =new Tile[str.length()];
        int i=0;
        for(char ch : str.toCharArray())
        {
            for(Tile t: p.getPlayerHand())
            {
                if(t.getLetter() == ch)
                {
                    tileArr[i] = t;
                    i++;
                    break;
                }
            }
        }
        return tileArr;
    }


}
