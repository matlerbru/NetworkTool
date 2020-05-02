package NetworkTool;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
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

    NetworkInterface.NIC tempNic = new NetworkInterface.NIC();
    NetworkInterface.NIC lastSetup = new NetworkInterface.NIC();
    ProfileContainer profile = new ProfileContainer();

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

    @FXML
    private ListView profileSelect;

    @FXML
    private HBox NicSettings;

    @FXML
    private BorderPane resultPane;

    @FXML
    private BorderPane setupPane;

    @FXML
    private Label nicLabel;

    @FXML
    private Label ipLabel;

    @FXML
    private Label subnetLabel;

    @FXML
    private Label gatewayLabel;

    @FXML
    private Label macLabel;

    @FXML
    private Label dhcpLabel;

    @FXML
    private Label nameLabel;



    public void initialize () {
        try {
            setNicData();
            updateProfileFromFile(".Profile.xml");
            Path path = Paths.get(".Profile.xml");
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);

            NicSettings.setHgrow(resultPane, Priority.ALWAYS);
            NicSettings.setHgrow(setupPane, Priority.ALWAYS);

            setupPane.widthProperty().addListener((obs, oldVal, newVal) -> {
                if ((double) newVal < 376) {
                    macLabel.setVisible(false);
                    dhcpLabel.setVisible(false);
                    nameLabel.setVisible(false);
                } else {
                    macLabel.setVisible(true);
                    dhcpLabel.setVisible(true);
                    nameLabel.setVisible(true);
                }

                if ((double) newVal < 282) {
                    nicLabel.setVisible(false);
                    ipLabel.setVisible(false);
                    subnetLabel.setVisible(false);
                    gatewayLabel.setVisible(false);
                } else {
                    nicLabel.setVisible(true);
                    ipLabel.setVisible(true);
                    subnetLabel.setVisible(true);
                    gatewayLabel.setVisible(true);
                }
            });

        } catch (Exception e) {
        }
    }

    public void setNicData() {
        NIC.getItems().clear();
        for (int i = 0; i < NetworkInterface.NIC.size(); i++) {
            NIC.getItems().add(NetworkInterface.NIC.get(i).getName());
        }
    }

    public void updateNicSettings () {
        int index = NIC.getSelectionModel().getSelectedIndex();

        NetworkInterface.clone(tempNic, NetworkInterface.NIC.get(index));

        macAdress.setText(NetworkInterface.NIC.get( index ).getMAC());
        dhcp.setSelected(NetworkInterface.NIC.get( index ).isDhcp());
        setIpFieldsEditable(!dhcp.isSelected());
        name.setText(NetworkInterface.NIC.get( index ).getDisplayName());

        dhcp.setDisable(false);
        name.setDisable(false);
        setIpFieldsEditable(!NetworkInterface.NIC.get(index).isDhcp());
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
                IP.setText(NetworkInterface.NIC.get( index ).getIPaddress());
                subnetMask.setText(NetworkInterface.NIC.get( index ).getSubnetMask());
                defaultGateway.setText(NetworkInterface.NIC.get( index ).getDefaultGateway());
            } else {
                if (!isFormattedAsIp(tempNic.getIPaddress())) {
                    IP.setText("0.0.0.0");
                } else IP.setText(tempNic.getIPaddress());
                if (!isFormattedAsIp(tempNic.getSubnetMask())) {
                    subnetMask.setText("0.0.0.0");
                } else subnetMask.setText(tempNic.getSubnetMask());
                if (!isFormattedAsIp(tempNic.getDefaultGateway())) {
                    defaultGateway.setText("0.0.0.0");
                } else defaultGateway.setText(tempNic.getDefaultGateway());
            }
        }
    }

    public void dhcpEvent () {
        setIpFieldsEditable(!dhcp.isSelected());
        tempNic.setDhcp(dhcp.isSelected());
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }

    public void ipEvent () {
        if (isFormattedAsIp(IP.getText()))  {
            tempNic.setIPaddress(IP.getText());
        } else if (IP.getText().length() < 7) {
            IP.setText("0.0.0.0");
        }else {
            IP.setText(tempNic.getIPaddress());
        }
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }

    public void subnetMaskEvent () {
        if (isFormattedAsIp(subnetMask.getText()))  {
            tempNic.setSubnetMask(subnetMask.getText());
        } else if (subnetMask.getText().length() < 7) {
            subnetMask.setText("0.0.0.0");
        }else {
            subnetMask.setText(tempNic.getSubnetMask());
        }
        defaultButton.setDisable(false);
        applytButton.setDisable(false);
    }

    public void DefaultGatewayEvent () {
        if (isFormattedAsIp(defaultGateway.getText())) {
            tempNic.setDefaultGateway(defaultGateway.getText());
        } else if (defaultGateway.getText().length() < 7) {
            defaultGateway.setText("0.0.0.0");
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
        try {
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
        } catch (Exception e) {
            return false;
        }

    }

    public void defaultButtonEvent () {
        if (tempNic.getName() != null) {
            NetworkInterface.clone(tempNic, NetworkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
            setUiTo(tempNic);
            defaultButton.setDisable(true);
            applytButton.setDisable(true);
        }
    }

    public void revertButtonEvent () throws IOException {
        if (lastSetup.getName() != null) {
            revertButton.setDisable(true);
            NetworkInterface.clone(tempNic, lastSetup);
            NetworkInterface.pushNIC(tempNic, NIC.getSelectionModel().getSelectedIndex());
            NetworkInterface.updateNIC(NIC.getSelectionModel().getSelectedIndex());
            setUiTo(NetworkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
            defaultButton.setDisable(true);
            applytButton.setDisable(true);
        }
    }

    public void setUiTo (NetworkInterface.NIC nic, boolean updateName) {
        NetworkInterface.clone(tempNic, nic);
        dhcp.setSelected(nic.isDhcp());
        setIpFieldsEditable(!nic.isDhcp());
        if (updateName) name.setText(nic.getDisplayName());
        macAdress.setText(nic.getMAC());
    }

    public void setUiTo (NetworkInterface.NIC nic) {
        setUiTo(nic, true);
    }

    public void applyButtonEvent () throws IOException {
        NetworkInterface.clone(lastSetup, NetworkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
        revertButton.setDisable(false);
        NetworkInterface.pushNIC(tempNic, NIC.getSelectionModel().getSelectedIndex());
        NetworkInterface.updateNIC(NIC.getSelectionModel().getSelectedIndex());
        setUiTo(NetworkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex()));
        defaultButton.setDisable(true);
        applytButton.setDisable(true);
    }

    public void AddProfileButtonEvent () throws IOException {
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
            errorAlert.setContentText("please try again with another name");
            errorAlert.showAndWait();
        } catch (RuntimeException e) {
        }
    }

    public void deleteProfileButtonEvent () throws IOException {
        int index = profileSelect.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            profile.removeProfile(index);
            profileSelect.getItems().remove(index);
            ProfileContainer.removeProfileFromFile(".Profile.xml", index);
        }
    }

    public void loadProfileButtonEvent () {
        int index = profileSelect.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            System.out.println(index);
            setUiTo(profile.getProfile(index), false);
        }
    }

    private void updateProfileFromFile (String fileName) throws IOException {
        ProfileContainer.Profiles profiles = new ProfileContainer.Profiles();
        profiles = ProfileContainer.loadProfilesFromFile(fileName);
        for (int i = 0; i < profiles.size(); i++) {

            profile.addProfile(profiles.getNic(i), profiles.getProfileName(i));
            profileSelect.getItems().add(profiles.getProfileName(i));
        }
    }
}