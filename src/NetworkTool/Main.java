package NetworkTool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    static final int WINDOW_WIDTH = 630;
    static final int WINDOW_HEIGHT = 225;

    @Override
    public void start(Stage primaryStage) throws Exception {
        NetworkInterface.updateNIC();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainSelector.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Network tool");
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.setMinHeight(230);
        primaryStage.setMinWidth(470);
        primaryStage.show();
        MainSelector controller = loader.<MainSelector>getController();
        ProfileContainer.loadProfilesFromFile(".profile.xml");

    }

    public static void main(String[] args) {
        launch(args);
    }
}
