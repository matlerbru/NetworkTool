package org.mlb.NetworkTool;

import org.mlb.ProfileFileManager.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class NicTool {

    NetworkInterfaceController tempNic = new NetworkInterfaceController();
    NetworkInterfaceController lastSetup = new NetworkInterfaceController();

    @FXML
    private ComboBox<String> nicSelector;

    @FXML
    private TextField macAdress;

    @FXML
    private CheckBox dhcp;

    @FXML
    private TextField name;

    @FXML
    private TextField ip;

    @FXML
    private TextField subnetMask;

    @FXML
    private TextField defaultGateway;

    @FXML
    private Button applyButton;

    @FXML
    private ListView profileSelect;

    @FXML
    private HBox NicSettings;

    @FXML
    private BorderPane resultPane;

    @FXML
    private BorderPane setupPane;

    @FXML
    private Button loadProfileButton;

    @FXML
    private Button addProfileButton;

    @FXML
    private Button removeProfileButton;

    EventHandler<ActionEvent> AddProfileButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            TextInputDialog dialog = new TextInputDialog("Enter profile name.");
            dialog.setTitle("Enter profile name");
            dialog.setContentText("Name:");
            Optional<String> result = dialog.showAndWait();
            try {
                ProfileContainer.container.addProfile(tempNic, result.get().trim());
                profileSelect.getItems().add(result.get());
                SaveProfileToFile.save(".profile.xml", tempNic, result.get());

            } catch (IllegalArgumentException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Duplicate name");
                errorAlert.setContentText("Please try again with another name");
                errorAlert.showAndWait();
            } catch (RuntimeException e) {
            }
        }
    };

    EventHandler<ActionEvent> deleteProfileButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            int index = profileSelect.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                String profileName = getProfileNameFromComboBox();
                ProfileContainer.container.removeProfile(profileName);
                profileSelect.getItems().remove(index);
                RemoveProfileFromFile.remove(".Profile.xml", index);
            }
        }
    };

    EventHandler<ActionEvent> loadProfileButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            String profileName = getProfileNameFromComboBox();
            System.out.println(profileName);
            if (profileName != null) {
                NetworkInterfaceController nic = ProfileContainer.container.getProfile(profileName);
                setUiTo(nic, false, false);
            }
        }
    };

    public void initialize () {
        try {
            LoadProfilesFromFile.load(".profile.xml");
            updateProfileFromFile(".profile.xml");
            setNicData();
            Path path = Paths.get(".Profile.xml");
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);

            NicSettings.setHgrow(resultPane, Priority.ALWAYS);
            NicSettings.setHgrow(setupPane, Priority.ALWAYS);

            applyButton.setOnAction(applyButtonHandler);

            loadProfileButton.setOnAction(loadProfileButtonHandler);
            addProfileButton.setOnAction(AddProfileButtonHandler);
            removeProfileButton.setOnAction(deleteProfileButtonHandler);

            ip.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    ipHandler();
                }
            });

            subnetMask.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    subnetMaskHandler();
                }
            });

            defaultGateway.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    DefaultGatewayEvent();
                }
            });

            name.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    nameHandler();
                }
            });

            dhcp.selectedProperty().addListener((obs, oldVal, newVal) -> {
                dhcpHandler();
            });

            nicSelector.getSelectionModel().selectedIndexProperty().addListener( (obs, oldVal, newVal) -> {
                updateNicSettings();
            });


        } catch (Exception e) {
        }
    }

    private void setNicData() {
        nicSelector.getItems().clear();
        for (NetworkInterfaceController nic : NetworkInterface.getSystemNetworkInterfaceControllers()) {
            nicSelector.getItems().add(nic.getName());
        }
    }

    private void updateNicSettings () {
        int index = nicSelector.getSelectionModel().getSelectedIndex();

        NetworkInterfaceController.clone(tempNic, NetworkInterface.getSystemNetworkInterfaceControllers().get(index));

        macAdress.setText(NetworkInterface.getSystemNetworkInterfaceControllers().get( index ).getMAC());
        dhcp.setSelected(NetworkInterface.getSystemNetworkInterfaceControllers().get( index ).isDhcp());
        setIpFieldsEditable(!dhcp.isSelected());
        name.setText(NetworkInterface.getSystemNetworkInterfaceControllers().get( index ).getDisplayName());

        dhcp.setDisable(false);
        name.setDisable(false);
        setIpFieldsEditable(!NetworkInterface.getSystemNetworkInterfaceControllers().get(index).isDhcp());
    }

    private void setIpFieldsEditable (boolean editable) {
        if (nicSelector.getSelectionModel().getSelectedIndex() >= 0){
            ip.setEditable(editable);
            subnetMask.setEditable(editable);
            defaultGateway.setEditable(editable);

            ip.setDisable(!editable);
            subnetMask.setDisable(!editable);
            defaultGateway.setDisable(!editable);

            if (!editable) {
                int index = nicSelector.getSelectionModel().getSelectedIndex();
                ip.setText(NetworkInterface.getSystemNetworkInterfaceControllers().get( index ).getIPaddress());
                subnetMask.setText(NetworkInterface.getSystemNetworkInterfaceControllers().get( index ).getSubnetMask());
                defaultGateway.setText(NetworkInterface.getSystemNetworkInterfaceControllers().get( index ).getDefaultGateway());
            } else {
                if (!isFormattedAsIp(tempNic.getIPaddress())) {
                    ip.setText("0.0.0.0");
                } else ip.setText(tempNic.getIPaddress());
                if (!isFormattedAsIp(tempNic.getSubnetMask())) {
                    subnetMask.setText("0.0.0.0");
                } else subnetMask.setText(tempNic.getSubnetMask());
                if (!isFormattedAsIp(tempNic.getDefaultGateway())) {
                    defaultGateway.setText("0.0.0.0");
                } else defaultGateway.setText(tempNic.getDefaultGateway());
            }
        }
    }

    private void dhcpHandler() {
        setIpFieldsEditable(!dhcp.isSelected());
        tempNic.setDhcp(dhcp.isSelected());
        applyButton.setDisable(false);
    }

    private void ipHandler() {
        if (isFormattedAsIp(ip.getText()))  {
            tempNic.setIPaddress(ip.getText());
        } else if (ip.getText().length() < 7) {
            this.ip.setText("0.0.0.0");
        }else {
            this.ip.setText(tempNic.getIPaddress());
        }
        applyButton.setDisable(false);
    }

    private void subnetMaskHandler() {
        if (isFormattedAsIp(subnetMask.getText()))  {
            tempNic.setSubnetMask(subnetMask.getText());
        } else if (subnetMask.getText().length() < 7) {
            subnetMask.setText("0.0.0.0");
        }else {
            subnetMask.setText(tempNic.getSubnetMask());
        }
        applyButton.setDisable(false);
    }

    private void DefaultGatewayEvent() {
        if (isFormattedAsIp(defaultGateway.getText())) {
            tempNic.setDefaultGateway(defaultGateway.getText());
        } else if (defaultGateway.getText().length() < 7) {
            defaultGateway.setText("0.0.0.0");
        } else {
            defaultGateway.setText(tempNic.getDefaultGateway());
        }
        applyButton.setDisable(false);
    }

    private void nameHandler() {
        tempNic.setDisplayName(name.getText());
        applyButton.setDisable(false);
    }

    private boolean isFormattedAsIp(String field) {
        try {
            for (int i = 0; i < 3; i++) {
                if (field.contains(".")) {
                    int dot = field.indexOf(".");
                    String string = field.substring(0, dot);
                    int value = Integer.parseInt(string);
                    if (value < 0 || value > 255) {
                        throw new IOException();
                    }
                    field = field.substring(dot + 1);
                } else {
                    throw new IOException();
                }
            }
            int value = Integer.parseInt(field);
            if (value < 0 || value > 255) {
                throw new IOException();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    EventHandler<ActionEvent> applyButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            NetworkInterfaceController.clone(lastSetup, NetworkInterface.getSystemNetworkInterfaceControllers().get(nicSelector.getSelectionModel().getSelectedIndex()));
            try {
                NetworkInterface.pushNIC(tempNic, nicSelector.getSelectionModel().getSelectedIndex());
                NetworkInterface.updateNic(nicSelector.getSelectionModel().getSelectedIndex());
                setUiTo(NetworkInterface.getSystemNetworkInterfaceControllers().get(nicSelector.getSelectionModel().getSelectedIndex()));
                applyButton.setDisable(true);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    };

    private void setUiTo (NetworkInterfaceController nic, boolean updateName, boolean updateMac) {
        NetworkInterfaceController.clone(tempNic, nic);
        dhcp.setSelected(nic.isDhcp());
        setIpFieldsEditable(!nic.isDhcp());
        if (updateName) {
            name.setText(nic.getDisplayName());
        }
        if (updateMac) {
            macAdress.setText(nic.getMAC());
        }
    }

    private void setUiTo (NetworkInterfaceController nic, boolean updateName) {
        setUiTo(nic, updateName, true);
    }

    private void setUiTo (NetworkInterfaceController nic) {
        setUiTo(nic, true);
    }

    private void updateProfileFromFile (String fileName) throws IOException {
        for (String key : ProfileContainer.container.getListOfKeys()) {
            profileSelect.getItems().add(key);
        }
    }

    private String getProfileNameFromComboBox() {
        return (String)profileSelect.getSelectionModel().getSelectedItem();
    }

}