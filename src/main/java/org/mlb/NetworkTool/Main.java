package org.mlb.NetworkTool;

import org.mlb.NetworkInterfaceTool.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static TabSelector controller;

    static final int WINDOW_WIDTH = 630;
    static final int WINDOW_HEIGHT = 225;

    @Override
    public void start(Stage primaryStage) throws Exception {
        NetworkInterface.updateAllNic();
           
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TabSelector.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Network tool");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setMinHeight(230);
        primaryStage.setMinWidth(470);
        primaryStage.show();
        controller = loader.<TabSelector>getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
