package NetworkTool;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class NetworkScanner {

    public void initialize() {
        try {
            setNicData();
            NIC.getSelectionModel().selectFirst();
            progressBar.setVisible(false);

        } catch (Exception e) {
        }
        initTable();

        queue = new QueueSemaphore(5);
    }

    void initTable() {
        itemNameCol.setCellValueFactory(
                new PropertyValueFactory<networkLocation,String>("Name")
        );
        itemIpAddrCol.setCellValueFactory(
                new PropertyValueFactory<networkLocation,String>("ipAddr")
        );
        itemMacAddrCol.setCellValueFactory(
                new PropertyValueFactory<networkLocation,String>("macAddr")
        );
        itemManufacturerCol.setCellValueFactory(
                new PropertyValueFactory<networkLocation,String>("Manufacturer")
        );

        tableData = FXCollections.observableArrayList();
        networkLocationTable.setItems(tableData);
    }

    volatile private QueueSemaphore queue;

    @FXML
    private ComboBox<String> NIC;

    @FXML
    private TextField rangeMin;

    @FXML
    private TextField rangeMax;

    @FXML
    private TextField timeout;

    @FXML
    public ProgressBar progressBar;

    @FXML
    private Button scanButton;

    ObservableList<networkLocation> tableData;
    @FXML
    private TableView<networkLocation> networkLocationTable;

    @FXML
    TableColumn itemNameCol;

    @FXML
    TableColumn itemIpAddrCol;

    @FXML
    TableColumn itemMacAddrCol;

    @FXML
    TableColumn itemManufacturerCol;

    public static class networkLocation {
        public SimpleStringProperty name = new SimpleStringProperty();
        public SimpleStringProperty ipAddr = new SimpleStringProperty();
        public SimpleStringProperty macAddr = new SimpleStringProperty();
        public SimpleStringProperty manufacturer = new SimpleStringProperty();

        private networkLocation (String name, String ipAddr, String macAddr, String manufacturer) {
            this.name = new SimpleStringProperty(name);
            this.ipAddr = new SimpleStringProperty(ipAddr);
            this.macAddr = new SimpleStringProperty(macAddr);
            this.manufacturer = new SimpleStringProperty(manufacturer);
        }

        public String getName() {
            return name.get();
        }

        public String getIpAddr() {
            return ipAddr.get();
        }

        public String getMacAddr() {
            return macAddr.get();
        }

        public String getManufacturer() {
            return manufacturer.get();
        }
    }

    private volatile boolean scanInProgress;

    public void setNicData() {
        NIC.getItems().clear();
        NIC.getItems().add("All");
        for (int i = 0; i < NetworkInterface.NIC.size(); i++) {
            NIC.getItems().add(NetworkInterface.NIC.get(i).getName());
        }
    }

    public void startScan() {
        if (scanInProgress) {
            scanInProgress = false;
        } else {
            scanInProgress = true;
            if (NIC.getSelectionModel().getSelectedIndex() == 0) {

                LinkedList<NetworkScannerService> scanners = new LinkedList<NetworkScannerService>();
                LinkedList<Thread> scans = new LinkedList<Thread>();

                for (int i = 0; i < NetworkInterface.NIC.size(); i++) {
                    NetworkScannerService scanner = new NetworkScannerService();
                    Thread scan = scanner.scan(i);

                    scanners.add(scanner);
                    scans.add(scan);
                    scans.get(i).start();
                }

                new Thread() {
                    @Override
                    public void run() {

                        updateGuiToStartScan();

                        while(true) {
                            threadSleep(10);

                            int scansRunning = getScansRunning(scans);
                            if (scansRunning == 0) {
                                break;
                            }
                            double progress = 0;
                            for (int i = 0; i <= NetworkInterface.NIC.size() - 1; i++) {
                                if (scanners.get(i).getProgress() != -1.0) {
                                    progress = progress + scanners.get(i).getProgress();
                                }
                            }
                            if (!scanInProgress) {
                                break;
                            }
                            progress = progress / scanners.size();
                            updateGuiProgressBar(progress);
                        }

                        updateGuiToEndScan();

                        scanInProgress = false;
                    }
                }.start();

            } else {

                NetworkScannerService scanner = new NetworkScannerService();
                Thread scan = scanner.scan(NIC.getSelectionModel().getSelectedIndex()-1);
                scan.start();

                new Thread(){
                    @Override
                    public void run(){
                        updateGuiToStartScan();
                        while(scan.isAlive()){
                            threadSleep(10);
                            updateGuiProgressBar(scanner.getProgress());
                        }
                        updateGuiToEndScan();
                        scanInProgress = false;
                    }
                }.start();
            }
        }
    }

    private void updateGuiProgressBar(double progress) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
            }
        });
    }

    private int getScansRunning(LinkedList<Thread> scans) {
        int threadsRunning = 0;
        for (int i = 0; i < scans.size(); i++) {
            if (scans.get(i).isAlive()) {
                threadsRunning++;
            }
        }
        return threadsRunning;
    }

    private void threadSleep(int time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
        }
    }

    private void updateGuiToStartScan() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tableData.clear();
                scanButton.setText("Stop");
                progressBar.setVisible(true);
            }
        });
    }

    private void updateGuiToEndScan() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                scanButton.setText("Scan");
                progressBar.setVisible(false);
            }
        });
    }

    private class NetworkScannerService {
        public NetworkScannerService() {
        }

        private double progress;

        public Thread scan(int nicIndex) {
            Thread scan = new Thread() {
                @Override
                public void run() {
                    NetworkInterface.NIC nic = new NetworkInterface.NIC();
                    NetworkInterface.clone(nic, NetworkInterface.NIC.get(nicIndex));
                    try {
                        progress = 0;
                        scanNetwork(nic);
                    } catch (Exception e) {
                    }
                }
            };
            return scan;
        }

        public double getProgress () {
            return progress;
        };

        private void scanNetwork(NetworkInterface.NIC nic) throws IOException {
        try {

            LinkedList<Thread> threads = new LinkedList<Thread>();

            for (int i = Integer.parseInt(rangeMin.getText()); i <= Integer.parseInt(rangeMax.getText()); i++) {
                try {
                    if (!scanInProgress)
                    {
                        throw new IllegalStateException();
                    }
                    loginAndWaitInQueue();
                    threads.add(startThreadAndPingDevice(nic, i));
                    threads.getLast().start();
                } catch (IllegalStateException e) {
                    break;
                } catch (Exception e) {
                    progress = -1.0;
                }
            }
            waitForAllThreadsToDie(threads);
        } catch (Exception e) {
            progress = -1.0;
        }
        }

        private Thread startThreadAndPingDevice(NetworkInterface.NIC nic, int i) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    String address = null;

                    address = formatIpAddress(address, nic);

                    address = address + i;

                    try {
                        pingDeviceAndGetInformation(address, nic);
                    } catch (NullPointerException e) {}

                    queue.logout();
                }
            };
            return thread;
        }

        private void pingDeviceAndGetInformation(String address, NetworkInterface.NIC nic) throws IllegalStateException {
            String hostName = getHostNameFromIp(address, Integer.parseInt(timeout.getText()));
            if ( hostName != null ) {
                String macAddr = getMacFromArpTable(address, nic);
                String manufacturer = getManufacturer(macAddr);
                if (!address.equals(nic.getIPaddress())) {
                    final networkLocation networkLocation = new networkLocation(hostName, address, macAddr, manufacturer);

                    if (scanInProgress)
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                tableData.add(networkLocation);
                            }
                        });
                    }
                }
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    double incrementBy = 1.0 / (Integer.parseInt(rangeMax.getText()) - Integer.parseInt(rangeMin.getText()) + 1);
                    progress = progress + incrementBy;
                }
            });

        }

        private InetAddress GetIpByName(String address, InetAddress ip) {
            try {
                ip = InetAddress.getByName(address);
            } catch (UnknownHostException e) {
            }
            return ip;
        }

        private String formatIpAddress(String address, NetworkInterface.NIC nic) {
            try {
                address = nic.getIPaddress().substring(0, ordinalIndexOf(nic.getIPaddress(), ".", 3) + 1);

            } catch (Exception e) {

            }
            return address;
        }

        private void loginAndWaitInQueue() {
            boolean loggedIn = queue.tryLogin();
            while (!loggedIn) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
                loggedIn = queue.tryLogin();
            }
        }
    }

    private void waitForAllThreadsToDie(LinkedList<Thread> threads) {
        while (true) {
            for (int i = 0; i < threads.size(); i++) {
                if (!threads.get(i).isAlive()) {
                    threads.remove(i);
                }
            }
            if (threads.size() == 0) {
                break;
            }
        }
    }

    public static int ordinalIndexOf(String string, String substring, int n) {
        try {
            int position = string.indexOf(substring);
            while (--n > 0 && position != -1)
                position = string.indexOf(substring, position + 1);
            return position;
        } catch (Exception e) {
            return -1;
        }
    }

    public void rangeMinEvent () {
        try {
            int range = Integer.parseInt(rangeMin.getText());
            if (range < 0 || range > 255) {
                throw new IOException();
            }
        } catch (Exception e) {
            rangeMin.setText("");
        }
    }

    public void rangeMaxEvent () {
        try {
            int range = Integer.parseInt(rangeMax.getText());
            if (range < 0 || range > 255) {
                throw new IOException();
            }
        } catch (Exception e) {
            rangeMin.setText("");
        }
    }

    public void timeoutEvent () {
        try {
            int timeout = Integer.parseInt(this.timeout.getText());
            if (timeout < 0 || timeout > Integer.MAX_VALUE) {
                throw new IOException();
            }
        } catch (Exception e) {
            timeout.setText("");
        }
    }

    private String getHostNameFromIp(String ipAddr, int timeout) {
        ProcessBuilder pb = new ProcessBuilder();
        String command = "ping -a -n 1 " + ipAddr + " -w " + timeout;
        System.out.println(command);
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
        }
        return null;
    }

    private String getMacFromArpTable (String ipAddr, NetworkInterface.NIC nic) {
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
            for (int i = 0; i <= NetworkInterface.NIC.size(); i++) {
                if (ipAddr.equals(NetworkInterface.NIC.get(i).getIPaddress())) {
                    return NetworkInterface.NIC.get(i).getMAC().toUpperCase();
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
                    int startString = ordinalIndexOf(line, "\"", 3) + 1;
                    int endString = ordinalIndexOf(line, "\"", 4);
                    manufacturer = line.substring(startString, endString);
                    break;
                }
            }
            return manufacturer;
        } catch (Exception e) {
            return "NA";
        }
    }

}

