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

    static final int WINDOW_WIDTH = 480;
    static final int WINDOW_HEIGHT = 180;

    @Override
    public void start(Stage primaryStage) throws Exception {

        networkInterface.updateNIC();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Network interface controller settings");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));

        primaryStage.show();






        Controller controller = loader.<Controller>getController();

    }


    public static void main(String[] args) {

        launch(args);

    }
}
