package org.mlb.NetworkInterfaceTool;

import org.mlb.Utility.*;

public class NetworkInterfaceController {

    public NetworkInterfaceController() {
    }

    public NetworkInterfaceController(String name, String displayName, String MAC, String IPaddress, boolean dhcp, String subnetMask, String defaultGateway) {
        this.name = name;
        this.displayName = displayName;
        this.MAC = MAC;
        this.IPaddress = IPaddress;
        this.dhcp = dhcp;
        this.subnetMask = subnetMask;
        this.defaultGateway = defaultGateway;
    }

    private String name;
    private String displayName;

    private String MAC;

    private String IPaddress;

    private boolean dhcp;

    private String subnetMask;
    private String defaultGateway;

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMac (String mac) {
        this.MAC = mac;
    }

    public String getIPaddress() {
        return IPaddress;
    }

    public void setIPaddress(String IPaddress) {
        this.IPaddress = IPaddress;
    }

    public boolean isDhcp() {
        return dhcp;
    }

    public void setDhcp(boolean dhcp) {
        this.dhcp = dhcp;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public String getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void clone(NetworkInterfaceController destination, NetworkInterfaceController source) {
        destination.name = source.name;
        destination.displayName = source.displayName;
        destination.dhcp = source.dhcp;
        destination.MAC = source.MAC;
        destination.IPaddress = source.IPaddress;
        destination.subnetMask = source.subnetMask;
        destination.defaultGateway = source.defaultGateway;
    }

    public static boolean isEqual(NetworkInterfaceController first, NetworkInterfaceController second) {
        return
            first.getDisplayName().equals(second.getDisplayName()) &&
            first.getName().equals(second.getName()) &&
            first.isDhcp() == second.isDhcp() &&
            first.isDhcp() ||
            (
                first.getIPaddress().equals(second.getIPaddress()) &&
                first.getDefaultGateway().equals(second.getDefaultGateway()) &&
                first.getSubnetMask().equals(second.getSubnetMask())
            );
    }

    public static NetworkInterfaceController getRandomNic() {
        NetworkInterfaceController nic = new NetworkInterfaceController();
        nic.setDisplayName(Random.getString(0, 20).trim());
        nic.setName(Random.getString(0, 20).trim());
        nic.setMac(Random.getMacAddress().trim());
        nic.setDhcp(Random.getBoolean());
        nic.setIPaddress(Random.getNetworkAddress());
        nic.setSubnetMask(Random.getNetworkAddress());
        nic.setDefaultGateway(Random.getNetworkAddress());
        return nic;
    }

}
