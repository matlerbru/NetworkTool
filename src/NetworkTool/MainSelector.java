package NetworkTool;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainSelector implements Initializable {

    @FXML
    private TabPane tabPane;

    @FXML
    private NicTool nicToolController;

    @FXML
    private NetworkScanner networkScannerController;

    public NetworkScanner getNetworkScanner() {
        return networkScannerController;
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkScannerController.setProgressBar(0.5);
    }

}