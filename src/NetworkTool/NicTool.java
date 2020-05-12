package NetworkTool;

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

    NetworkInterface.Nic tempNic = new NetworkInterface.Nic();
    NetworkInterface.Nic lastSetup = new NetworkInterface.Nic();
    ProfileContainer profile = new ProfileContainer();

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
    private Button revertButton;

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

    public void initialize () {
        try {
            ProfileContainer.loadProfilesFromFile(".profile.xml");
            setNicData();
            updateProfileFromFile(".Profile.xml");
            Path path = Paths.get(".Profile.xml");
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);

            NicSettings.setHgrow(resultPane, Priority.ALWAYS);
            NicSettings.setHgrow(setupPane, Priority.ALWAYS);

            revertButton.setOnAction(revertButtonHandler);
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
        for (NetworkInterface.Nic nic : NetworkInterface.getNic()) {
            nicSelector.getItems().add(nic.getName());
        }
    }

    private void updateNicSettings () {
        int index = nicSelector.getSelectionModel().getSelectedIndex();

        NetworkInterface.clone(tempNic, NetworkInterface.NIC.get(index));

        macAdress.setText(NetworkInterface.NIC.get( index ).getMAC());
        dhcp.setSelected(NetworkInterface.NIC.get( index ).isDhcp());
        setIpFieldsEditable(!dhcp.isSelected());
        name.setText(NetworkInterface.NIC.get( index ).getDisplayName());

        dhcp.setDisable(false);
        name.setDisable(false);
        setIpFieldsEditable(!NetworkInterface.NIC.get(index).isDhcp());
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
                ip.setText(NetworkInterface.NIC.get( index ).getIPaddress());
                subnetMask.setText(NetworkInterface.NIC.get( index ).getSubnetMask());
                defaultGateway.setText(NetworkInterface.NIC.get( index ).getDefaultGateway());
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

    EventHandler<ActionEvent> revertButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (lastSetup.getName() != null) {
                revertButton.setDisable(true);
                NetworkInterface.clone(tempNic, lastSetup);
                NetworkInterface.pushNIC(tempNic, nicSelector.getSelectionModel().getSelectedIndex());
                NetworkInterface.updateNIC(nicSelector.getSelectionModel().getSelectedIndex());
                setUiTo(NetworkInterface.NIC.get(nicSelector.getSelectionModel().getSelectedIndex()));
                applyButton.setDisable(true);
            }
        }
    };

    EventHandler<ActionEvent> applyButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            NetworkInterface.clone(lastSetup, NetworkInterface.NIC.get(nicSelector.getSelectionModel().getSelectedIndex()));
            revertButton.setDisable(false);
            try {
                NetworkInterface.pushNIC(tempNic, nicSelector.getSelectionModel().getSelectedIndex());
                NetworkInterface.updateNIC(nicSelector.getSelectionModel().getSelectedIndex());
                setUiTo(NetworkInterface.NIC.get(nicSelector.getSelectionModel().getSelectedIndex()));
                applyButton.setDisable(true);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        }
    };

    private void setUiTo (NetworkInterface.Nic nic, boolean updateName, boolean updateMac) {
        NetworkInterface.clone(tempNic, nic);
        dhcp.setSelected(nic.isDhcp());
        setIpFieldsEditable(!nic.isDhcp());
        if (updateName) {
            name.setText(nic.getDisplayName());
        }
        if (updateMac) {
            macAdress.setText(nic.getMAC());
        }
    }

    private void setUiTo (NetworkInterface.Nic nic, boolean updateName) {
        setUiTo(nic, updateName, true);
    }

    private void setUiTo (NetworkInterface.Nic nic) {
        setUiTo(nic, true);
    }

    EventHandler<ActionEvent> AddProfileButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            TextInputDialog dialog = new TextInputDialog("Enter profile name.");
            dialog.setTitle("Enter profile name");
            dialog.setContentText("Name:");
            Optional<String> result = dialog.showAndWait();
            try {
                profile.addProfile(tempNic, result.get().trim());
                profileSelect.getItems().add(result.get());
                ProfileContainer.saveProfileToFile(".profile.xml", tempNic, result.get());
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
                profile.removeProfile(index);
                profileSelect.getItems().remove(index);
                ProfileContainer.removeProfileFromFile(".Profile.xml", index);
            }
        }
    };

    EventHandler<ActionEvent> loadProfileButtonHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            int index = profileSelect.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                System.out.println(index);
                NetworkInterface.Nic tempProfile = profile.getProfile(index);
                setUiTo(profile.getProfile(index), false, false);
            }
        }
    };

    private void updateProfileFromFile (String fileName) throws IOException {
        ProfileContainer.Profiles profiles = new ProfileContainer.Profiles();
        profiles = ProfileContainer.loadProfilesFromFile(fileName);
        for (int i = 0; i < profiles.size(); i++) {
            profile.addProfile(profiles.getNic(i), profiles.getProfileName(i));
            profileSelect.getItems().add(profiles.getProfileName(i));
        }
    }
}