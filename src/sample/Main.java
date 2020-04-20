package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class Main extends Application {

    static final int WINDOW_WIDTH = 630;
    static final int WINDOW_HEIGHT = 225;

    @Override
    public void start(Stage primaryStage) throws Exception {
        sample.networkInterface.updateNIC();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Network interface controller settings");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
        sample.Controller controller = loader.<sample.Controller>getController();
        sample.ProfileContainer.loadProfilesFromFile(".profile.xml");
    }

    public static void main(String[] args) {
        launch(args);
    }



}
