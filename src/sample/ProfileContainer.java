package sample;

import java.io.*;
import java.util.ArrayList;

public class ProfileContainer {
    public ProfileContainer() {

    }
    private ArrayList<networkInterface.NIC> nic = new ArrayList<networkInterface.NIC>();
    private ArrayList<String> name = new ArrayList<String>();

    public void addProfile(networkInterface.NIC nic, String name) {
        networkInterface.NIC temp = new networkInterface.NIC();
        networkInterface.clone(temp, nic);
        this.nic.add(temp);
        this.name.add(name);
    }

    public networkInterface.NIC getProfile(int index) {
        System.out.println(name.get(index));
        networkInterface.printNIC(nic.get(index));
        return nic.get(index);
    }

    public networkInterface.NIC getProfile(String name) {
        int index = name.indexOf(name);
        return getProfile(index);
    }

    public void removeProfile (int index) {
        this.nic.remove(index);
        this.name.remove(index);
    }

    public void removeProfile (String name) {
        int index = name.indexOf(name);
        removeProfile(index);
    }

    public static void saveProfileToFile(String fileName, networkInterface.NIC nic, String name) throws IOException {
        File createFile = new File(fileName);
        boolean fileCreated = createFile.createNewFile();

        PrintStream fileWriter = new PrintStream(new FileOutputStream(fileName, true));

        if (fileCreated) {
            System.out.println("File created: " + fileName);
            fileWriter.println("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
            fileWriter.println();
        } else System.out.println("File already exists");
        fileWriter.println("<profile>");
        fileWriter.println("    <profileName>" + name + "</profile>");
        fileWriter.println("    <name>" + nic.getName() + "</name>");
        fileWriter.println("    <displayName>" + nic.getDisplayName() + "</displayName>");
        fileWriter.println("    <DHCP>" + nic.isDhcp() + "</DHCP>");
        if (!nic.isDhcp()) {
            fileWriter.println("    <IP>" + nic.getIPaddress() + "</IP>");
            fileWriter.println("    <subnetMask>" + nic.getSubnetMask() + "</subnetMask>");
            fileWriter.println("    <defaultGateway>" + nic.getDefaultGateway() + "</defaultGateway>");
        }
        fileWriter.println("</profile>");

        fileWriter.println();
        fileWriter.flush();
        fileWriter.close();
    }

    public void loadProfilesFromFile(File file, networkInterface.NIC nic) {
    }
}