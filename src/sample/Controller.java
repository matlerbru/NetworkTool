package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Box;

import java.io.IOException;
import java.math.MathContext;

public class Controller {

    networkInterface.NIC tempNic = new networkInterface.NIC();
    networkInterface.NIC lastSetup = new networkInterface.NIC();
    private boolean lastSetupSet = true;

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

    @FXML
    private Button revertButton;

    @FXML
    private Button defaultButton;

    @FXML
    private Button applytButton;

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

        dhcp.setDisable(false);
        name.setDisable(false);
        setIpFieldsEditable(!networkInterface.NIC.get(index).isDhcp());
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
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }

    public void ipEvent () {
        if (isFormattedAsIp(IP.getText()))  {
            tempNic.setIPaddress(IP.getText());
        } else {
            IP.setText(tempNic.getIPaddress());
        }
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }

    public void subnetMaskEvent () {
        if (isFormattedAsIp(subnetMask.getText()))  {
            tempNic.setSubnetMask(subnetMask.getText());
        } else {
            subnetMask.setText(tempNic.getSubnetMask());
        }
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }

    public void DefaultGatewayEvent () {
        if (isFormattedAsIp(defaultGateway.getText()))  {
            tempNic.setDefaultGateway(defaultGateway.getText());
        } else {
            defaultGateway.setText(tempNic.getDefaultGateway());
        }
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }

    public void nameEvent () {
        tempNic.setDisplayName(name.getText());
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }


    public boolean isFormattedAsIp (String field) {
        String temp = field;
        int dotCount = 0;
        int lastDot = -1;
        for (int i = 0; i < temp.length(); i++) {
            if (temp.charAt(i) == '.') {
                dotCount++;
                String subString = temp.substring(lastDot + 1, i);
                if (!subString.matches("\\d+") && !subString.isBlank()) {
                    return false;
                } else {
                    if (!subString.isBlank()) {
                        if (Integer.parseInt(subString) > 255) {
                            return false;
                        }
                    }
                }
                lastDot = i;
            }
        }
        if (dotCount != 3) return false;
        if (temp.substring(lastDot + 1).matches("\\d+") || temp.substring(lastDot + 1).isBlank()) {
            if (!temp.substring(lastDot + 1).isBlank()) {
                if (Integer.parseInt(temp.substring(lastDot + 1)) > 255) {
                    return false;
                }
            }
            return true;
        } else return false;
    }

    public void defaultButtonEvent () {
        if (tempNic.getName() != null) {
            networkInterface.clone(tempNic, networkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
            setUiTo(tempNic);
            defaultButton.setDisable(true);
            applytButton.setDisable(true);
        }
    }

    public void revertButtonEvent () throws IOException {
        if (lastSetup.getName() != null) {
            revertButton.setDisable(true);
            networkInterface.clone(tempNic, lastSetup);
            networkInterface.pushNIC(tempNic, NIC.getSelectionModel().getSelectedIndex());
            networkInterface.updateNIC(NIC.getSelectionModel().getSelectedIndex());
            setUiTo(networkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
            defaultButton.setDisable(true);
            applytButton.setDisable(true);
        }
    }

    public void setUiTo (networkInterface.NIC nic) {
        networkInterface.clone(tempNic, nic);
        dhcp.setSelected(nic.isDhcp());
        setIpFieldsEditable(!nic.isDhcp());
        name.setText(nic.getDisplayName());
        macAdress.setText(nic.getMAC());
    }

    public void applyButtonEvent () throws IOException {
        networkInterface.clone(lastSetup, networkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
        revertButton.setDisable(false);
        networkInterface.pushNIC(tempNic, NIC.getSelectionModel().getSelectedIndex());
        networkInterface.updateNIC(NIC.getSelectionModel().getSelectedIndex());
        setUiTo(networkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
        defaultButton.setDisable(true);
        applytButton.setDisable(true);
    }
}
