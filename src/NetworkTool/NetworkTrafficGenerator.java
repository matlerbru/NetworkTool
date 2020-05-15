package NetworkTool;

import javafx.fxml.Initializable;

import java.net.*;
import java.util.ResourceBundle;
import NetworkTrafficGenerator.UdpPacketSenderThread;


public class NetworkTrafficGenerator implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        UdpPacketSenderThread packageSender = new UdpPacketSenderThread(1);


        packageSender.setName("UDP packet sender");
        packageSender.setPriority(1);
        packageSender.setMessageLength(10);
        packageSender.setPort(8000);
        packageSender.setTargetAddress("192.168.1.62");


        packageSender.start();



    }

}