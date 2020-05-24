package org.mlb.NetworkTool;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.*;
import java.util.ResourceBundle;
import org.mlb.NetworkTrafficGenerator.UdpPacketSenderThread;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import static javafx.application.Platform.runLater;

public class NetworkTrafficGenerator implements Initializable {

    @FXML
    private HBox trafficGenerator;

    @FXML
    private BorderPane resultPane;

    @FXML
    private BorderPane setupPane;

    @FXML
    private ComboBox nicSelector;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private RadioButton udp;

    @FXML
    private RadioButton tcp;

    @FXML
    private Button startGenerator;

    private UdpPacketSenderThread packageSender;

    private final ToggleGroup group = new ToggleGroup();

    EventHandler<ActionEvent> startGeneratorHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (startGenerator.getText().equals("Start")) {
                startGenerator();
            } else if (startGenerator.getText().equals("Stop")) {
                stopGenerator();
            }
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trafficGenerator.setHgrow(resultPane, Priority.ALWAYS);
        trafficGenerator.setHgrow(setupPane, Priority.ALWAYS);

        setNicData();
        nicSelector.getSelectionModel().selectFirst();

        ip.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                ipHandler();
            }
        });

        port.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                portHandler();
            }
        });

        startGenerator.setOnAction(startGeneratorHandler);
        initRadiobuttonGroup();
    }

    private void startGenerator() {
        packageSender = new UdpPacketSenderThread(1);
        packageSender.setName("UDP packet sender");
        packageSender.setPriority(1);

        packageSender.setPort(Integer.parseInt(port.getText()));
        packageSender.setTargetAddress(ip.getText());
        packageSender.start();

        runLater(() -> startGenerator.setText("Stop"));
    }

    private void stopGenerator() {
        packageSender.interrupt();
        runLater(() -> startGenerator.setText("Start"));
    }

    private void setNicData() {
        nicSelector.getItems().clear();
        nicSelector.getItems().add("Any");
        for (NetworkInterfaceController nic : NetworkInterface.getSystemNetworkInterfaceControllers()) {
            nicSelector.getItems().add(nic.getName());
        }
    }

    private void ipHandler() {
        try {
            Inet4Address.getByName(ip.getText());
        } catch (UnknownHostException e) {
            ip.setText("");
        }
    }

    private void portHandler() {
        try {
            int port = Integer.parseInt(this.port.getText());
            if (port < 0 || port > 65535) {
                throw new NumberFormatException("Port number out of range");
            }
        } catch (NumberFormatException e) {
            port.setText("8000");
        }
    }

    private void initRadiobuttonGroup() {
        udp.setToggleGroup(group);
        udp.setSelected(true);

        tcp.setToggleGroup(group);
    }





}