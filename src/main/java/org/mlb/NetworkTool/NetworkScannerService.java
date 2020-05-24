package org.mlb.NetworkTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Scanner;

import static javafx.application.Platform.runLater;

public class NetworkScannerService {

    public NetworkScannerService() {
    }

    private double progress;

    private static QueueSemaphore queue = new QueueSemaphore(10);

    public double getProgress() {
        return progress;
    }

    public Thread scan(int nicIndex) {
        return new Thread(() -> {
            NetworkInterfaceController nic = new NetworkInterfaceController();
            NetworkInterfaceController.clone(nic, NetworkInterface.getSystemNetworkInterfaceControllers().get(nicIndex));
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
            for (int i = Main.controller.getNetworkScanner().getRangeMin(); i <= Main.controller.getNetworkScanner().getRangeMax(); i++) {
                try {
                    if (!Main.controller.getNetworkScanner().getScanInProgress())
                    {
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
            Utility.Threads.waitForAllThreadsToDie(threads);
        } catch (Exception e) {
            progress = -1.0;
        }
    }

    private Thread startThreadAndPingDevice(NetworkInterfaceController nic, int i) {
        return new Thread(() -> {
            try {
            String address = formatIpAddress(nic);
            address = address + i;
            pingDeviceAndGetInformation(address, nic);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                runLater(() -> {
                    double incrementBy = 1.0 / (Main.controller.getNetworkScanner().getRangeMax() - Main.controller.getNetworkScanner().getRangeMin() + 1);
                    progress = progress + incrementBy;
                });
            }
            queue.logout();
        });
    }

    private void pingDeviceAndGetInformation(String address, NetworkInterfaceController nic) throws IllegalStateException {
        String hostName = getHostNameFromIp(address, Main.controller.getNetworkScanner().getTimeout());
        if ( hostName != null ) {
            String macAddr = getMacFromArpTable(address, nic);
            String manufacturer = getManufacturer(macAddr);
            if (!address.equals(nic.getIPaddress())) {
                final NetworkLocation networkLocation = new NetworkLocation(hostName, address, macAddr, manufacturer);

                if (Main.controller.getNetworkScanner().getScanInProgress())
                {
                    boolean duplicateNetworkLocation = false;
                    for (int i = 0; i < Main.controller.getNetworkScanner().getTableSize(); i++) {
                        NetworkLocation temp = Main.controller.getNetworkScanner().getTableRow(i);
                        if (temp.getMacAddr().equals(networkLocation.getMacAddr())) {
                            duplicateNetworkLocation = true;
                        }
                    }
                    if (!duplicateNetworkLocation) {
                        Main.controller.getNetworkScanner().addToTable(networkLocation);
                    }
                }
            }
        }
        runLater(() -> {
            double incrementBy = 1.0 / (Main.controller.getNetworkScanner().getRangeMax() - Main.controller.getNetworkScanner().getRangeMin() + 1);
            progress = progress + incrementBy;
        });
    }

    private String formatIpAddress(NetworkInterfaceController nic) {
        try {
            return nic.getIPaddress().substring(0, Utility.ordinalIndexOf(nic.getIPaddress(), ".", 3) + 1);
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    private void loginAndWaitInQueue() {
        boolean loggedIn = queue.tryLogin();
        while (!loggedIn) {
            Utility.Threads.sleep(10);
            loggedIn = queue.tryLogin();
        }
    }

    private String getHostNameFromIp(String ipAddr, int timeout) {
        ProcessBuilder pb = new ProcessBuilder();
        String command = "ping -a -n 1 " + ipAddr + " -w " + timeout;
        pb.command("cmd.exe", "/c", command);
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String hostName = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0) {
                    if (line.contains("Destination host unreachable")){
                        break;
                    }
                    if (line.contains("Pinging")) {
                        if (line.contains("[") && line.contains("]")) {
                            hostName = line.substring(8, line.indexOf("[") - 1);
                        } else {
                            hostName = "";
                        }
                    }
                    if (line.contains("Reply from ")) {
                        return hostName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getMacFromArpTable (String ipAddr, NetworkInterfaceController nic) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("cmd.exe", "/c", "arp -a");
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean correctInterface = false;
            while ((line = reader.readLine()) != null) {
                if (line.length() != 0) {
                    if (line.contains("Interface") && line.contains(nic.getIPaddress())) {
                        correctInterface = true;
                    } else if (line.contains("Interface") && !line.contains(nic.getIPaddress())) {
                        correctInterface = false;
                    }
                    if (correctInterface && line.contains(ipAddr)) {
                        line = line.replace(ipAddr, "");
                        line = line.trim();
                        int index = line.indexOf(" ");
                        line = line.substring(0, index);

                        return line.toUpperCase();
                    }
                }
            }
            for (int i = 0; i <= NetworkInterface.getSystemNetworkInterfaceControllers().size(); i++) {
                if (ipAddr.equals(NetworkInterface.getSystemNetworkInterfaceControllers().get(i).getIPaddress())) {
                    return NetworkInterface.getSystemNetworkInterfaceControllers().get(i).getMAC().toUpperCase();
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private String getManufacturer(String macAddr) {
        try {
            String manufacturer = null;
            String mac = macAddr;
            mac = mac.replace("-", ":");
            mac = mac.substring(0, 8);
            File file = new File("MacAddress.xml");
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(mac)) {
                    int startString = Utility.ordinalIndexOf(line, "\"", 3) + 1;
                    int endString = Utility.ordinalIndexOf(line, "\"", 4);
                    manufacturer = line.substring(startString, endString);
                    break;
                }
            }
            return manufacturer;
        } catch (Exception e) {
            e.printStackTrace();
            return "NA";
        }
    }

}
