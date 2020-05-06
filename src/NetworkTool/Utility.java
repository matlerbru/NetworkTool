package NetworkTool;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class Utility {

    public static class Threads {

        public static void sleep(int time) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
/*
    public static <T> T getController(String FXMLfile) {
        FXMLLoader loader = new FXMLLoader(Utility.class.getResourceAsStream(FXMLfile));
        try {
            Parent root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        return loader.getController();
    }
*/
}
