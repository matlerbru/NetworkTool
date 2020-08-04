package org.mlb.NetworkScanner;


import org.mlb.NetworkTool.*;
import org.mlb.Utility.*;
import org.mlb.NetworkInterfaceTool.*;

import java.util.LinkedList;

import static javafx.application.Platform.runLater;

public class NetworkScannerService {

    public NetworkScannerService(int scanTimeout) {
        this.scanTimeout = scanTimeout;
    }

    private int scanTimeout;

    private double progress;

    private static QueueSemaphore queue = new QueueSemaphore(10);

    public double getProgress() {
        return progress;
    }

    public Thread scan(int nicIndex) {
        return new Thread(() -> {
            NetworkInterfaceController nic = new NetworkInterfaceController();
            NetworkInterfaceController.clone(nic,
                    NetworkInterface.getSystemNetworkInterfaceControllers().get(nicIndex));
            try {
                progress = 0;
                scanNetwork(nic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void scanNetwork(NetworkInterfaceController nic) {
        try {

            LinkedList<Thread> threads = new LinkedList<>();
            for (int i = Main.controller.getNetworkScanner().getRangeMin(); i <= Main.controller.getNetworkScanner()
                    .getRangeMax(); i++) {
                try {
                    if (!Main.controller.getNetworkScanner().getScanInProgress()) {
                        throw new IllegalStateException();
                    }
                    loginAndWaitInQueue();
                    threads.add(startThreadAndPingDevice(nic, i));
                    threads.getLast().start();
                } catch (IllegalStateException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    progress = -1.0;
                }
            }
            Threads.waitForAllToDie(threads);
        } catch (Exception e) {
            progress = -1.0;
        }
    }

    private Thread startThreadAndPingDevice(NetworkInterfaceController nic, int i) {
        return new Thread(() -> {
            try {
                String address = formatIpAddress(nic);
                address = address + i;
                NetworkLocation networkLocation = new PingDeviceAndGetInformation().start(address, scanTimeout);
                if (networkLocation != null) {
                    Main.controller.getNetworkScanner().addToTable(networkLocation);    
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } finally {
                runLater(() -> {
                    double incrementBy = 1.0 / (Main.controller.getNetworkScanner().getRangeMax()
                            - Main.controller.getNetworkScanner().getRangeMin() + 1);
                    progress += incrementBy;
                });
            }
            queue.logout();
        });
    }

    private String formatIpAddress(NetworkInterfaceController nic) {
        try {
            return nic.getIPaddress().substring(0, OrdinalIndexOf.ordinalIndexOf(nic.getIPaddress(), ".", 3) + 1);
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    private void loginAndWaitInQueue() {
        boolean loggedIn = queue.tryLogin();
        while (!loggedIn) {
            Threads.sleep(10);
            loggedIn = queue.tryLogin();
        }
    }



}
