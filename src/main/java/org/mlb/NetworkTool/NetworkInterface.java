package org.mlb.NetworkTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class NetworkInterface {

    private static ArrayList<NetworkInterfaceController> systemNetworkInterfaceControllers = new ArrayList<NetworkInterfaceController>();

    public static ArrayList<NetworkInterfaceController> getSystemNetworkInterfaceControllers () {
        return systemNetworkInterfaceControllers;
    }

    public static void updateAllNic() {
        systemNetworkInterfaceControllers.clear();
        int index = 0;
        while (true) {
            try {
                ReadNetworkInterfaceControllerFromCmd readNic = new ReadNetworkInterfaceControllerFromCmd();
                NetworkInterfaceController nic = readNic.get(index);
                systemNetworkInterfaceControllers.add(index, nic);
                index++;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public static void updateNic(int index) {
        ReadNetworkInterfaceControllerFromCmd readNic = new ReadNetworkInterfaceControllerFromCmd();
        NetworkInterfaceController nic = readNic.get(index);
        systemNetworkInterfaceControllers.remove(index);
        systemNetworkInterfaceControllers.add(index, nic);
    }

    public static void pushNIC (NetworkInterfaceController nic, int index) throws IllegalStateException {
        String name = systemNetworkInterfaceControllers.get(index).getDisplayName();

        String IpCommand = "netsh interface ipv4 set address name=\"" + name + "\"";
        String NameCommand = "netsh interface set interface name=\"" + name + "\" newname=\"" + nic.getDisplayName() + "\"";

        System.out.println(NameCommand);

        if (nic.isDhcp()) {
            IpCommand = IpCommand + " dhcp";
        } else {
            IpCommand = IpCommand + " static " + nic.getIPaddress() + " " + nic.getSubnetMask() + " " + nic.getDefaultGateway();
        }

        try {
            System.out.println(IpCommand);
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("cmd.exe", "/c", IpCommand);
            Process processIp = pb.start();
            pb.command("cmd.exe", "/c", NameCommand);
            Process processName = pb.start();

            BufferedReader readerIp = new BufferedReader(new InputStreamReader(processIp.getInputStream()));

            String line;

            System.out.println();
            while ((line = readerIp.readLine()) != null) {
                System.out.println(line);
                if (line.length() > 0 && !line.contains("DHCP is already enabled on this interface.")) {
                    throw new IllegalStateException(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}