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
import java.util.Scanner;

public class NetworkScanner {

    ObservableList<networkLocation> data;

    public void initialize() {
        try {
            setNicData();
            NIC.getSelectionModel().selectFirst();
            progressBar.setVisible(false);

        } catch (Exception e) {
        }
        initTable();
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

        data = FXCollections.observableArrayList();
        networkLocationTable.setItems(data);
    }


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

    private boolean scanInProgress;

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
                for (int i = 1; i <= NetworkInterface.NIC.size(); i++) {
                    System.out.println("Scan nic: " + i);
                    int finalI = i;
                    new Thread() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    scanButton.setText("Stop");
                                }
                            });
                            NetworkInterface.NIC nic = new NetworkInterface.NIC();
                            NetworkInterface.clone(nic, NetworkInterface.NIC.get(finalI - 1));
                            try {
                                scanNetwork(nic);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    scanButton.setText("Scan");
                                }
                            });
                        }
                    }.start();
                }
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        scanButton.setText("Stop");
                    }
                });
                NetworkScannerService scanner = new NetworkScannerService();
                Thread scan = scanner.scan();
                scan.start();
            }
        }
    }

    private class NetworkScannerService {
        public NetworkScannerService() {
        }

        private int progress;

        public Thread scan() {
            Thread scan = new Thread() {
                @Override
                public void run() {
                    NetworkInterface.NIC nic = new NetworkInterface.NIC();
                    NetworkInterface.clone(nic, NetworkInterface.NIC.get(NIC.getSelectionModel().getSelectedIndex() - 1));
                    try {
                        scanNetwork(nic);
                    } catch (Exception e) {
                        System.out.print("Exception caught: ");
                        e.printStackTrace();
                    }
                    scanInProgress = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            scanButton.setText("Scan");
                        }
                    });
                }
            };
            return scan;
        }

        public double getProgress () {
            return progress;
        };

    }















    private void scanNetwork(NetworkInterface.NIC nic) throws IOException {
        data.clear();
        progressBar.setVisible(true);
        for (int i = Integer.parseInt(rangeMin.getText()); i <= Integer.parseInt(rangeMax.getText()); i++) {
            if (!scanInProgress) {
                System.out.println("Exception thrown: IllegalStateException");
                throw new IllegalStateException();
            }
            try {
                String address = nic.getIPaddress().substring(0, ordinalIndexOf(nic.getIPaddress(), ".", 3)+1);
                address = address + i;
                InetAddress ip = InetAddress.getByName(address);
                if (ip.isReachable(Integer.parseInt(timeout.getText()))) {
                    String name = ip.getHostName();
                    if (name.equals(address)) {
                        name = null;
                    }
                    String macAddr = getMacFromArpTable(address, nic);
                    String manufacturer = getManufacturer(macAddr);
                    if (!address.equals(nic.getIPaddress())) {
                        networkLocation networkLocation = new networkLocation(name, address, macAddr, manufacturer);
                        data.add(networkLocation);
                    }
                }
            } catch (Exception e) {
            }
            double progress = (i - Integer.parseInt(rangeMin.getText())) / Double.valueOf(Integer.parseInt(rangeMax.getText()) - Integer.parseInt(rangeMin.getText()));
            //progressBar.setProgress(progress);
        }
        //progressBar.setVisible(false);
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
            System.out.println("Exception");
            return "NA";
        }
    }

}

