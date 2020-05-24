package org.mlb.NetworkTrafficGenerator;

import org.mlb.NetworkTool.Utility;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpPacketSenderThread extends Thread {

    public UdpPacketSenderThread(int messageLength) {
        setMessageLength(messageLength);
    }

    private final static int UDP_MAX_MESSAGE_SIZE = 65507;

    private int port = -1;

    private InetAddress targetAddress = null;

    private byte[] message;

    public int getMessageLength() {
        return message.length;
    }

    private void setMessageLength(int messageLength) {
        calculateMessage(messageLength + 3);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        if (port >= 0 && port <= 65535){
            this.port = port;
        } else throw new IllegalArgumentException("Requested port is out of range");

    }

    public String getTargetAddress() {
        return targetAddress.getHostAddress();
    }

    public void setTargetAddress(String targetAddress) {
        try {
            this.targetAddress = InetAddress.getByName(targetAddress);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Requested address is out of range");
        }
    }

    @Override
    public void run(){

        throughputRegulatorThread regulator = new throughputRegulatorThread();
        regulator.start();
        try {
            if (port == -1 || targetAddress == null) {
                throw new InstantiationException("Required field not set");
            }
            DatagramPacket packet = new DatagramPacket(message, message.length, targetAddress, port);
            DatagramSocket socket = new DatagramSocket();
            while (true) {
                socket.send(packet);
                if (Thread.interrupted()) {
                    throw new InterruptedException("Interrupted");
                }
                Utility.Threads.sleep(1);
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("interrupt for fanden!!!!");
            regulator.interrupt();
        }
    }

    private void calculateMessage(int messageLength) {
        String messageToSend = "";
        for (int i = 3; i < messageLength; i++) {
            messageToSend = messageToSend + " ";
        }
        messageToSend = trimMessageSize(messageToSend);
        message = messageToSend.getBytes();
    }

    private String trimMessageSize(String messageToSend) {
        if (messageToSend.length() > UDP_MAX_MESSAGE_SIZE) {
            messageToSend = messageToSend.substring(0, UDP_MAX_MESSAGE_SIZE);
        }
        return messageToSend;
    }
}
