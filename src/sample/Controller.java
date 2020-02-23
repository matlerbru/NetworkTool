package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class Controller {

    @FXML
    private ComboBox<String> NIC;

    public void setData() {
        for (int i = 0; i < networkInterface.NIC.size(); i++) {
            NIC.getItems().add( networkInterface.NIC.get(i).getName() );
        }
    }



}
