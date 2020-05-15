package NetworkTool;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.*;
import java.util.ResourceBundle;
import NetworkTrafficGenerator.UdpPacketSenderThread;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import static javafx.application.Platform.runLater;

public class NetworkTrafficGenerator implements Initializable {

    @FXML
    private HBox trafficGenerator;

    @FXML
    private BorderPane resultPane;

    @FXML
    private BorderPane setupPane;

    @FXML
    private TextField ip;

    @FXML
    private TextField port;

    @FXML
    private Button startGenerator;

    private UdpPacketSenderThread packageSender;




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trafficGenerator.setHgrow(resultPane, Priority.ALWAYS);
        trafficGenerator.setHgrow(setupPane, Priority.ALWAYS);

        startGenerator.setOnAction(startGeneratorHandler);
    }

    EventHandler<ActionEvent> startGeneratorHandler = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            if (startGenerator.getText().equals("Start")) {
                startGenerator();
            } else if (startGenerator.getText().equals("Stop")) {
                stopGenerator();
            }
        }
    };

    private void startGenerator() {
        packageSender = new UdpPacketSenderThread(1000);
        packageSender.setName("UDP packet sender");
        packageSender.setPriority(1);


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

}