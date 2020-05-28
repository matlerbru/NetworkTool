package org.mlb.NetworkTrafficGenerator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.TimedSemaphore;
import org.mlb.NetworkTool.Main;

public class UdpPacketSenderThread extends Thread {

    public UdpPacketSenderThread(int amountOfThreads) {
        this.amountOfThreads = amountOfThreads;
    }

    private int amountOfThreads;

    private final static int UDP_MAX_MESSAGE_SIZE = 65507;

    private int port = -1;

    private InetAddress targetAddress = null;

    private byte[] message;

    private LinkedList<UdpSocketThread> sender = new LinkedList<>();

    private static final TimedSemaphore semaphore = new TimedSemaphore(1, TimeUnit.SECONDS, 1);

    private double totalThroughput;

    public int getMessageLength() {
        return message.length;
    }

    public void setMessageLength(int messageLength) {
        calculateMessage(messageLength + 3);
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        if (port >= 0 && port <= 65535) {
            this.port = port;
        } else
            throw new IllegalArgumentException("Requested port is out of range");
    }

    public String getTargetAddress() {
        return targetAddress.getHostAddress();
    }

    public void setTargetAddress(final String targetAddress) {
        try {
            this.targetAddress = InetAddress.getByName(targetAddress);
        } catch (final UnknownHostException e) {
            throw new IllegalArgumentException("Requested address is out of range");
        }
    }

    public double getThroughput() {
        return totalThroughput;
    }

    @Override
    public void run(){
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(message, message.length, targetAddress, port);
            if (port == -1 || targetAddress == null) {
                throw new InstantiationException("Required field not set");
            }
            for (int i = 0; i < amountOfThreads-1; i++) { 
                sender.add(i, new UdpSocketThread(socket, packet));
                sender.get(i).start();
            }
            while (true) {    
                semaphore.acquire();
                calculateThroughput();
                if (Thread.interrupted()) {
                    throw new InterruptedException("Interrupted");
                }
            }
        } catch (final InterruptedException e) {
            for (Thread thread : sender) {
                thread.interrupt();
                Main.controller.NetworkTrafficGenerator().setThroughput(0);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateThroughput() {
        double totalMessagesSent = 0.0;
        for (UdpSocketThread thread : sender) {
            totalMessagesSent += (double)thread.getMessagesSentAndReset();
        }
        totalThroughput = totalMessagesSent*((message.length+28.0)*8.0)/1000000.0;
        Main.controller.NetworkTrafficGenerator().setThroughput(totalThroughput);
    }

    private void calculateMessage(final int messageLength) {
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
