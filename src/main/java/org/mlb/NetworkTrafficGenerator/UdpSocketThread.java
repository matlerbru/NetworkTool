package org.mlb.NetworkTrafficGenerator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.concurrent.TimedSemaphore;

class UdpSocketThread extends Thread {

    protected UdpSocketThread(DatagramSocket socket, DatagramPacket packet, int frequency) {
        this.socket = socket;
        this.packet = packet;

        setSemaphore(frequency);
        amountOfCreatedThreads++;
        setName("UDP socket thread " + amountOfCreatedThreads);
    }

    private static TimedSemaphore semaphore;

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
    
    private void setSemaphore(int frequency){
        if (frequency > 1000) {
            semaphore = new TimedSemaphore(10000/(frequency), TimeUnit.MILLISECONDS, 10);
        } else {
            semaphore = new TimedSemaphore(1000/frequency, TimeUnit.MILLISECONDS, 1);
        }
    }
}