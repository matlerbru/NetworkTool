package sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class Profile {
    private ArrayList<networkInterface.NIC> profile = new ArrayList<networkInterface.NIC>();

    public Profile() {

    }

    public void addProfile(networkInterface.NIC nic) {
        profile.add(nic);
    }

    public networkInterface.NIC getProfile(int index) {
        return profile.get(index);
    }

    public void deleteProfile(int index) {
        profile.remove(index);
    }

    public void deleteProfile(String nic) {
        profile.remove(nic.indexOf(nic));
    }


    public void saveProfileToFile(File file, networkInterface.NIC nic, String name) throws IOException {
        PrintStream fileStream = new PrintStream(new File(file.getName()));
        boolean fileCreated = file.createNewFile();
        if (fileCreated) {
            System.out.println("File created: " + file.getName());
            fileStream.println("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
        } else System.out.println("File already exists");

        fileStream.println("<profile>" + name + "</profile>");
        fileStream.println("    <name>" + nic.getName() + "</name>");
        fileStream.println("    <displayName>" + nic.getDisplayName() + "</displayName>");
        fileStream.println("    <MAC>" + nic.getMAC() + "</MAC>");
        fileStream.println("    <DHCP>" + nic.isDhcp() + "</DHCP>");
        if (nic.isDhcp()) {
            fileStream.println("    <IP>" + nic.getIPaddress() + "</IP>");
            fileStream.println("    <subnetMask>" + nic.getSubnetMask() + "</subnetMask>");
            fileStream.println("    <defaultGateway>" + nic.getDefaultGateway() + "</defaultGateway>");
        }
        fileStream.close();
    }
    public void loadProfilesFromFile(File file, networkInterface.NIC nic) {
    }
}