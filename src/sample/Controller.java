package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.shape.Box;

import java.io.IOException;

public class Controller {

    networkInterface.NIC tempNic = new networkInterface.NIC();
    networkInterface.NIC lastSetup = new networkInterface.NIC();

    @FXML
    private ComboBox<String> NIC;

    @FXML
    private TextField macAdress;

    @FXML
    private CheckBox dhcp;

    @FXML
    private TextField name;

    @FXML
    private TextField IP;

    @FXML
    private TextField subnetMask;

    @FXML
    private TextField defaultGateway;

    public void initialize () {
        setNicData();

    }

    public void setNicData() {
        NIC.getItems().clear();
        for (int i = 0; i < networkInterface.NIC.size(); i++) {
            NIC.getItems().add(networkInterface.NIC.get(i).getName());
        }
    }

    public void updateNicSettings () {
        int index = NIC.getSelectionModel().getSelectedIndex();

        networkInterface.clone(tempNic, networkInterface.NIC.get(index));

        macAdress.setText(networkInterface.NIC.get( index ).getMAC());
        dhcp.setSelected(networkInterface.NIC.get( index ).isDhcp());
        setIpFieldsEditable(!dhcp.isSelected());
        name.setText(networkInterface.NIC.get( index ).getDisplayName());
    }

    public void setIpFieldsEditable (boolean editable) {
        if (NIC.getSelectionModel().getSelectedIndex() >= 0){
            IP.setEditable(editable);
            subnetMask.setEditable(editable);
            defaultGateway.setEditable(editable);

            IP.setDisable(!editable);
            subnetMask.setDisable(!editable);
            defaultGateway.setDisable(!editable);

            if (!editable) {
                int index = NIC.getSelectionModel().getSelectedIndex();
                IP.setText(networkInterface.NIC.get( index ).getIPaddress());
                subnetMask.setText(networkInterface.NIC.get( index ).getSubnetMask());
                defaultGateway.setText(networkInterface.NIC.get( index ).getDefaultGateway());
            } else {
                IP.setText(tempNic.getIPaddress());
                subnetMask.setText(tempNic.getSubnetMask());
                defaultGateway.setText(tempNic.getDefaultGateway());
            }
        }
    }

    public void dhcpEvent () {
        setIpFieldsEditable(!dhcp.isSelected());
        tempNic.setDhcp(dhcp.isSelected());
        networkInterface.printNIC(tempNic);

        networkInterface.printNIC(tempNic);
    }

    public void ipEvent () {
        if (isFormattedAsIp(IP.getText()))  {
            tempNic.setIPaddress(IP.getText());
        } else {
            IP.setText(tempNic.getIPaddress());
        }
    }

    public void subnetMaskEvent () {
        if (isFormattedAsIp(subnetMask.getText()))  {
            tempNic.setSubnetMask(subnetMask.getText());
        } else {
            subnetMask.setText(tempNic.getSubnetMask());
        }
    }

    public void DefaultGatewayEvent () {
        if (isFormattedAsIp(defaultGateway.getText()))  {
            tempNic.setDefaultGateway(defaultGateway.getText());
        } else {
            defaultGateway.setText(tempNic.getDefaultGateway());
        }
    }

    public boolean isFormattedAsIp (String field) {
        return true;
    }

    public void defaultButtonEvent () {
        if (tempNic.getName() != null) {
            networkInterface.clone(tempNic, networkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
            setUiTo(tempNic);
        }
    }

    public void revertButtonEvent () throws IOException {
        if (lastSetup.getName() != null) {
            networkInterface.clone(tempNic, lastSetup);
            networkInterface.pushNIC(tempNic, NIC.getSelectionModel().getSelectedIndex());
            networkInterface.updateNIC(NIC.getSelectionModel().getSelectedIndex());
            setUiTo(networkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
        }
    }

    public void setUiTo (networkInterface.NIC nic) {
        networkInterface.clone(tempNic, nic);
        dhcp.setSelected(nic.isDhcp());
        setIpFieldsEditable(!nic.isDhcp());
        name.setText(nic.getDisplayName());
    }
}
