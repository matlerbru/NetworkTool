package NetworkTool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class TabSelector implements Initializable {

    @FXML
    private TabPane tabPane;

    @FXML
    private NicTool nicToolController;

    public NicTool getNicTool() {
        return nicToolController;
    }

    @FXML
    private NetworkScanner networkScannerController;

    public NetworkScanner getNetworkScanner() {
        return networkScannerController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}