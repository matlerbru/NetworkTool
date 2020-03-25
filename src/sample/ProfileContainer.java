package sample;

import java.io.*;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ProfileContainer {
    public ProfileContainer() {

    }

    private ArrayList<networkInterface.NIC> nic = new ArrayList<networkInterface.NIC>();
    private ArrayList<String> name = new ArrayList<String>();

    public void addProfile(networkInterface.NIC nic, String name) {
        if (this.name.contains(name)) {
            System.out.println("false");
            throw new IllegalArgumentException("Duplicate profile name");
        } else {
            System.out.println("true");
            networkInterface.NIC temp = new networkInterface.NIC();
            networkInterface.clone(temp, nic);
            this.nic.add(temp);
            this.name.add(name);
        }
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

        createFile(fileName);

        PrintStream fileWriter = new PrintStream(new FileOutputStream(fileName, true));

        fileWriter.println("<profile>");
        fileWriter.println("    <profileName>" + name + "</profileName>");
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

    private static void createFile(String fileName, boolean header) throws IOException {
        File file = new File(fileName);
        boolean fileCreated = file.createNewFile();

        PrintStream fileWriter = new PrintStream(new FileOutputStream(fileName, true));

        if (fileCreated) {
            System.out.println("File created: " + fileName);
            if (header) {
                fileWriter.println("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
                fileWriter.println();
            }
        }
        fileWriter.flush();
        fileWriter.close();
    }

    private static void createFile(String fileName) throws IOException {
        createFile(fileName, true);
    }

    public static class Profiles {
        public Profiles() {
        }

        private ArrayList<networkInterface.NIC> nic = new ArrayList<networkInterface.NIC>();
        private ArrayList<String> profileName = new ArrayList<String>();

        public void addNic(networkInterface.NIC nic, String profileName) {
            this.nic.add(nic);
            this.profileName.add(profileName);
        }

        public networkInterface.NIC getNic (int index) {
            return nic.get(index);
        }

        public String getProfileName (int index) {
            return profileName.get(index);
        }

        public int size () {
            return nic.size();
        }
    }

    public static Profiles loadProfilesFromFile(String fileName) throws IOException {

        createFile(fileName);

        File file = new File(fileName);
        Scanner fileReader = new Scanner(file);

        Profiles profiles = new Profiles();
        networkInterface.NIC tempNic = new networkInterface.NIC();
        String tempProfileName = new String("");
        boolean readingProfile = false;

        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            if (line.contains("<profile>")) readingProfile = true;
            if (line.contains("</profile>")) {
                readingProfile = false;
                profiles.nic.add(tempNic);
                profiles.profileName.add(tempProfileName);
            }
            if (readingProfile) {
                if (line.contains("<profileName>")) {
                    String temp = line.replace("    <profileName>", "");
                    temp = temp.replace("</profileName>", "");
                    tempProfileName = temp;

                } else if (line.contains("<name>")) {
                    String temp = line.replace("    <name>", "");
                    temp = temp.replace("</name>", "");
                    tempNic.setName(temp);

                } else if (line.contains("<displayName>")) {
                    String temp = line.replace("    <displayName>", "");
                    temp = temp.replace("</displayName>", "");
                    tempNic.setDisplayName(temp);

                } else if (line.contains("<DHCP>")) {
                    String temp = line.replace("    <DHCP>", "");
                    temp = temp.replace("</DHCP>", "");
                    tempNic.setDhcp(temp.contains("true"));

                } else if (line.contains("<IP>")) {
                    String temp = line.replace("    <IP>", "");
                    temp = temp.replace("</IP>", "");
                    tempNic.setIPaddress(temp);

                } else if (line.contains("<subnetMask>")) {
                    String temp = line.replace("    <subnetMask>", "");
                    temp = temp.replace("</subnetMask>", "");
                    tempNic.setSubnetMask(temp);

                } else if (line.contains("<defaultGateway>")) {
                    String temp = line.replace("    <defaultGateway>", "");
                    temp = temp.replace("</defaultGateway>", "");
                    tempNic.setDefaultGateway(temp);
                }

                System.out.println();
            }
        }
        fileReader.close();
        return profiles;
    }

    public static void removeProfileFromFile(String fileName, int index) throws IOException {createFile(fileName);

        File file = new File(fileName);
        Scanner fileReader = new Scanner(file);

        File tempFile = new File("temp" + fileName);

        boolean readingProfile = false;
        int readingIndex = -1;
        int lineNumber = 0;

        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();

            if (line.contains("<profile>")) readingIndex++;

            if (!(readingIndex == index)) {
                createFile(tempFile.getName(), false);
                PrintStream fileWriter = new PrintStream(new FileOutputStream(tempFile, true));
                fileWriter.println(line);
                fileWriter.close();
            }
        }

        fileReader.close();
        Path path = Paths.get(fileName);
        Path tempPath = Paths.get(tempFile.getName());
        Files.delete(path);
        Files.move(tempPath, tempPath.resolveSibling(fileName));
        Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
    }
 }
