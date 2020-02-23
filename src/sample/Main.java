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

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();

        networkInterface.updateNIC();


        networkInterface.NIC testObject = new networkInterface.NIC("test disp name.", "navn", "syg mac addresse", "192.168.1.80", false, "255.255.255.255", "192.168.1.1");



        //networkInterface.printNIC();

        networkInterface.pushNIC(testObject, 2);

    }


    public static void main(String[] args) {
        launch(args);
    }
}
