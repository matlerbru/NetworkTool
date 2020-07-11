package org.mlb.NetworkTool;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.*;
import java.util.ResourceBundle;
import org.mlb.NetworkTrafficGenerator.UdpPacketSenderThread;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import static javafx.application.Platform.runLater;

public class NetworkTrafficGenerator implements Initializable {

    @FXML
    private HBox generator;

    @FXML
    private BorderPane generatorPane;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private TextField messageLength;

    @FXML
    private TextField frequency;

    @FXML
    private RadioButton udp;

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

        generator.setHgrow(generatorPane, Priority.ALWAYS);

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

        messageLength.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                messageLengthHandler();
            }
        });

        
        frequency.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                frequencyHandler();
            }
        });

        startGenerator.setOnAction(startGeneratorHandler);
        initRadiobuttonGroup();
    }

    private void startGenerator() {
        packageSender = new UdpPacketSenderThread(20);
        packageSender.setMessageLength(Integer.parseInt(this.messageLength.getText()) -28 );
        packageSender.setName("UDP packet sender");
        packageSender.setPriority(1);

        packageSender.setPort(Integer.parseInt(port.getText()));
        packageSender.setTargetAddress(ip.getText());
        packageSender.setFrequency(Integer.parseInt(frequency.getText()));
        packageSender.start();

        runLater(() -> startGenerator.setText("Stop"));
    }

    private void stopGenerator() {
        packageSender.interrupt();
        runLater(() -> startGenerator.setText("Start"));
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

    private void messageLengthHandler() {
        try {
            int length = Integer.parseInt(this.messageLength.getText());
            if (length < 28 || length > 65535) {
                throw new NumberFormatException("Port number out of range");
            }
        } catch (NumberFormatException e) {
            messageLength.setText("28");
        }
    }

    private void frequencyHandler() {
        try {
            int frequency = Integer.parseInt(this.frequency.getText());
            if (frequency < 1 || frequency > 10000) {
                throw new NumberFormatException("Frequency not valid");
            }
        } catch (NumberFormatException e) {
            frequency.setText("1000");
        }
    }

    private void initRadiobuttonGroup() {
        udp.setToggleGroup(group);
        udp.setSelected(true);
    }

}