package org.mlb.NetworkTool;

import org.mlb.Utility.*;

import org.mlb.NetworkScanner.*;
import org.mlb.NetworkInterfaceTool.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    @FXML
    private HBox networkScanner;

    @FXML
    private ComboBox<String> nicSelector;

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

    void initializeTable() {
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

    EventHandler<ActionEvent> scanHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            if (scanInProgress) {
                scanInProgress = false;
            } else {
                scanInProgress = true;
                startScan();
            }
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNicData();
        nicSelector.getSelectionModel().selectFirst();
        progressBar.setVisible(false);
        initializeTable();

        networkScanner.setHgrow(resultPane, Priority.ALWAYS);
        networkScanner.setHgrow(setupPane, Priority.ALWAYS);

        scanButton.setOnAction(scanHandler);

        rangeMin.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                ipRangeHandler(rangeMin, 0);
            }
        });

        rangeMax.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                ipRangeHandler(rangeMax, 254);
            }
        });

        timeout.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                timeoutHandler();
            }
        });
    }

    private void setNicData() {
        nicSelector.getItems().clear();
        nicSelector.getItems().add("All");
        for (NetworkInterfaceController nic : NetworkInterface.getSystemNetworkInterfaceControllers()) {
            nicSelector.getItems().add(nic.getName());
        }
    }

    public void startScan() {
            if (nicSelector.getSelectionModel().getSelectedIndex() == 0) {
                scanAllNic();
            } else {
                scanSingleNic();
            }
    }

    private void scanAllNic() {
        LinkedList<NetworkScannerService> scanners = new LinkedList<>();
        LinkedList<Thread> scans = new LinkedList<>();

        for (int i = 0; i < NetworkInterface.getSystemNetworkInterfaceControllers().size(); i++) {
            NetworkScannerService scanner = new NetworkScannerService(getTimeout());
            Thread scan = scanner.scan(i);

            scanners.add(scanner);
            scans.add(scan);
            scans.get(i).start();
        }

        new Thread(() -> {
            updateGuiToStartScan();
            while(true) {
                Threads.sleep(10);
                int scansRunning = Threads.getAmountAlive(scans);
                if (scansRunning == 0 || !scanInProgress) {
                    break;
                }
                double progress = 0;
                for (int i = 0; i <= NetworkInterface.getSystemNetworkInterfaceControllers().size() - 1; i++) {
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
    }

    private void scanSingleNic() {
        NetworkScannerService scanner = new NetworkScannerService(getTimeout());
        Thread scan = scanner.scan(nicSelector.getSelectionModel().getSelectedIndex()-1);
        scan.start();

        new Thread(() -> {
            updateGuiToStartScan();
            while(scan.isAlive() && scanInProgress){
                Threads.sleep(10);
                setProgressBar(scanner.getProgress());
            }
            updateGuiToEndScan();
            scanInProgress = false;
        }).start();
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

    public void ipRangeHandler(TextField range, int faultValue){
        try {
            int value = Integer.parseInt(range.getText());
            if (value < 0 || value > 254) {
                throw new IOException();
            }
        } catch (Exception e) {
            range.setText(String.valueOf(faultValue));
        }
    }

    public void timeoutHandler() {
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

