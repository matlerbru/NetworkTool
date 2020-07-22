package org.mlb.NetworkInterfaceTool;

import javafx.beans.property.SimpleStringProperty;

public class NetworkLocation {
    public SimpleStringProperty name = new SimpleStringProperty();
    public SimpleStringProperty ipAddr = new SimpleStringProperty();
    public SimpleStringProperty macAddr = new SimpleStringProperty();
    public SimpleStringProperty manufacturer = new SimpleStringProperty();

    public NetworkLocation(String name, String ipAddr, String macAddr, String manufacturer) {
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
