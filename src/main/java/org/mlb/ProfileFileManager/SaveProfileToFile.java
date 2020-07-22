package org.mlb.ProfileFileManager;

import org.mlb.NetworkInterfaceTool.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class SaveProfileToFile {

    private PrintStream ps;

    public void save(String fileName, NetworkInterfaceController nic, String name) {
        try {
            createFile(fileName);
            ps = new PrintStream(new FileOutputStream(fileName, true));
            printToFile(nic, name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file = CreateXmlFile.createXmlFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printToFile(NetworkInterfaceController nic, String name) {
        ps.println("<profile>");
        ps.println("    <profileName>" + name + "</profileName>");
        ps.println("    <name>" + nic.getName() + "</name>");
        ps.println("    <displayName>" + nic.getDisplayName() + "</displayName>");
        ps.println("    <DHCP>" + nic.isDhcp() + "</DHCP>");
        if (!nic.isDhcp()) {
            ps.println("    <IP>" + nic.getIPaddress() + "</IP>");
            ps.println("    <subnetMask>" + nic.getSubnetMask() + "</subnetMask>");
            ps.println("    <defaultGateway>" + nic.getDefaultGateway() + "</defaultGateway>");
        }
        ps.println("</profile>");

        ps.println();
        ps.flush();
        ps.close();
    }
}
