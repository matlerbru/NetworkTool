package org.mlb.NetworkTrafficGenerator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.TimedSemaphore;

class UdpSocketThread extends Thread {

    protected UdpSocketThread(DatagramSocket socket, DatagramPacket packet) {
        this.socket = socket;
        this.packet = packet;
        amountOfCreatedThreads++;
        setName("UDP socket thread " + amountOfCreatedThreads);
    }

    private static final TimedSemaphore semaphore = new TimedSemaphore(10, TimeUnit.MICROSECONDS, 1);

    private static int amountOfCreatedThreads;

    private DatagramSocket socket;

    private DatagramPacket packet;

    private int messagesSent;

    protected int getMessagesSentAndReset() {
        int temp = messagesSent;
        messagesSent = 0;
        return temp;
    }

    @Override
    public void run(){
        try {
            while (true) {
                semaphore.acquire();
                socket.send(packet);
                messagesSent++;
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                if (semaphore.getAvailablePermits() > 1) {
                    System.out.println("time constrain overstepped");
                }
            }
        } catch (InterruptedException e) {
            amountOfCreatedThreads = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}