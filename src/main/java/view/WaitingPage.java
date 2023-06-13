package view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Model;
import view_model.ViewModel;

public class WaitingPage extends Application {

    ViewModel vm = ViewModel.getViewModel();
    private GamePage gp = GamePage.getGP();
    private View v = View.getView();
    public static Stage theStage;
    @FXML
    private Label waitingLabel;

    @FXML
    private Button startButton;

    public VBox root;

    private boolean isHost = false;


    public static void main(String[] args) {
        launch();
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        theStage = primaryStage;
        root = new VBox(10);
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/gameGui.css").toExternalForm());
        primaryStage.setScene(scene);
        if (isHost) {
            waitingLabel = new Label("Waiting for Players to Join");
            startButton = new Button("Start Game");

        } else {
            waitingLabel = new Label("Waiting for Host to Start");
            startButton = new Button("Join Game");
        }
        waitingLabel.getStyleClass().add("waiting-label");
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(waitingLabel,startButton);
        startButton.setVisible(isHost ? true : false);
        primaryStage.show();
        setWaitingDisplay();
    }

    public void setWaitingDisplay() {
        startButton.setOnAction(e -> {
            if(isHost) {
                //TODO: do NOT change the methods call order!!
                gp.start(theStage);
                vm.getModel().getHostServer().hostPlayer.initPlayersHand();
                v.setViewModel();
                vm.initPlayersBoard();
                Thread t = new Thread(() -> {
                    //Goal: waiting with the initGame until a player put tiles on board(otherwise msg is null)
                    // TODO: seperating from initGame the msg from board
                    synchronized (gp.getLockObject()) {
                        try {
                            gp.getLockObject().wait(); // Releases the lock and waits until notified
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        vm.getModel().getHostServer().hostPlayer.initGame();
                    }
                });
                t.start();
            }

        });
    }


    public void setHost(boolean isHost) {
        this.isHost = isHost;
    }

}