package NetworkTool;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import static javafx.application.Platform.*;

public class NetworkScanner implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNicData();
        NIC.getSelectionModel().selectFirst();
        progressBar.setVisible(false);
        initTable();

        networkScanner.setHgrow(resultPane, Priority.ALWAYS);
        networkScanner.setHgrow(setupPane, Priority.ALWAYS);
    }

    void initTable() {
        itemNameCol.setCellValueFactory(
                new PropertyValueFactory<NetworkLocation,String>("Name")
        );
        itemIpAddrCol.setCellValueFactory(
                new PropertyValueFactory<NetworkLocation,String>("ipAddr")
        );
        itemMacAddrCol.setCellValueFactory(
                new PropertyValueFactory<NetworkLocation,String>("macAddr")
        );
        itemManufacturerCol.setCellValueFactory(
                new PropertyValueFactory<NetworkLocation,String>("Manufacturer")
        );

        tableData = FXCollections.observableArrayList();
        networkLocationTable.setItems(tableData);
    }

    @FXML
    private HBox networkScanner;

    @FXML
    private ComboBox<String> NIC;

    @FXML
    private BorderPane setupPane;

    @FXML
    private BorderPane resultPane;

    @FXML
    private TextField rangeMax;

    public int getRangeMax () {
        return getTextFieldAsInteger(rangeMax);
    }

    @FXML
    private TextField rangeMin;

    public int getRangeMin () {
        return getTextFieldAsInteger(rangeMin);
    }

    @FXML
    private TextField timeout;

    public int getTimeout () {
        return getTextFieldAsInteger(timeout);
    }

    private int getTextFieldAsInteger(TextField text) {
        return Integer.parseInt(text.getText());
    }

    @FXML
    public ProgressBar progressBar;

    public void setProgressBar(double progress) {
        runLater(() -> progressBar.setProgress(progress));
    }

    @FXML
    private Button scanButton;

    ObservableList<NetworkLocation> tableData;
    @FXML
    private TableView<NetworkLocation> networkLocationTable;

    @FXML
    private TableColumn itemNameCol;

    @FXML
    private TableColumn itemIpAddrCol;

    @FXML
    private TableColumn itemMacAddrCol;

    @FXML
    private TableColumn itemManufacturerCol;

    public void addToTable(NetworkLocation networkLocation){
        runLater(() -> tableData.add(networkLocation));
    }

    public NetworkLocation getTableRow(int rowIndex) {
        return networkLocationTable.getItems().get(rowIndex);
    }

    public int getTableSize() {
        return networkLocationTable.getItems().size();
    }

    private volatile boolean scanInProgress;

    public boolean getScanInProgress() {
        return scanInProgress;
    }

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

                LinkedList<NetworkScannerService> scanners = new LinkedList<>();
                LinkedList<Thread> scans = new LinkedList<>();

                for (int i = 0; i < NetworkInterface.NIC.size(); i++) {
                    NetworkScannerService scanner = new NetworkScannerService();
                    Thread scan = scanner.scan(i);

                    scanners.add(scanner);
                    scans.add(scan);
                    scans.get(i).start();
                }

                new Thread(() -> {
                    updateGuiToStartScan();
                    while(true) {
                        Utility.Threads.sleep(10);
                        int scansRunning = Utility.Threads.getAmountOfThreadsAlive(scans);
                        if (scansRunning == 0 || !scanInProgress) {
                            break;
                        }
                        double progress = 0;
                        for (int i = 0; i <= NetworkInterface.NIC.size() - 1; i++) {
                            if (scanners.get(i).getProgress() != -1.0) {
                                progress = progress + scanners.get(i).getProgress();
                            }
                        }
                        progress = progress / scanners.size();

                        progressBar.setProgress(progress);
                    }
                    updateGuiToEndScan();
                    scanInProgress = false;
                }).start();

            } else {

                NetworkScannerService scanner = new NetworkScannerService();
                Thread scan = scanner.scan(NIC.getSelectionModel().getSelectedIndex()-1);
                scan.start();

                new Thread(() -> {
                    updateGuiToStartScan();
                    while(scan.isAlive() && scanInProgress){
                        Utility.Threads.sleep(10);
                        setProgressBar(scanner.getProgress());
                    }
                    updateGuiToEndScan();
                    scanInProgress = false;
                }).start();
            }
        }
    }

    private void updateGuiToStartScan() {
        runLater(() -> {
            tableData.clear();
            scanButton.setText("Stop");
            progressBar.setVisible(true);
        });
    }

    private void updateGuiToEndScan() {
        runLater(() -> {
            scanButton.setText("Scan");
            progressBar.setVisible(false);
        });
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
            if (timeout < 0) {
                throw new IOException();
            }
        } catch (Exception e) {
            timeout.setText("100");
        }

    }

}

