package model.network;
import javafx.application.Platform;
import model.concrete.GameState;
import model.concrete.Player;
import view.GamePage;
import view.Main;
import view.WaitingPage;

import java.io.*;
import java.net.*;


// Client class
public class Client extends Player {
    String ip;
    int port;
    String name;
    Socket socket;
    BufferedReader consoleReader;
    BufferedReader readFromServer;
    PrintWriter writeToServer;
    private GamePage gp = GamePage.getGP();
    private WaitingPage wp = new WaitingPage();


    public Client(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public void start() {
        try {
            socket = new Socket(ip, port);
            System.out.println("Connected to server.");

            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            readFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writeToServer = new PrintWriter(socket.getOutputStream(), true);


            // receiving msg (another thread?)
                    String message;
                    while ((message = readFromServer.readLine()) != null)
                    {
                        if(message.equals("/start"))
                        {
                            Platform.runLater(()->{
                                gp.start(WaitingPage.theStage);

                        });
                        }
                        if(message.equals("/query")){

                        }
                        else
                        {
                            System.out.println(message);
                        }
                     }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                writeToServer.println(socket.getLocalAddress()+"has left");
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}