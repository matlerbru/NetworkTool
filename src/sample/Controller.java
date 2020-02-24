package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class Controller {

    @FXML
    private ComboBox<String> NIC;

    public void initialize () {
        setNicData();




    }

    public void setNicData() {
        NIC.getItems().clear();
        for (int i = 0; i < networkInterface.NIC.size(); i++) {
            NIC.getItems().add( networkInterface.NIC.get(i).getName() );
        }
    }

    public void test () {
        System.out.println(NIC.getButtonCell());
    }





}
