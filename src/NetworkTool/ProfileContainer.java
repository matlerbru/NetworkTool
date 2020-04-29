package NetworkTool;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ProfileContainer {
    public ProfileContainer() {

    }

    private ArrayList<NetworkInterface.NIC> nic = new ArrayList<NetworkInterface.NIC>();
    private ArrayList<String> name = new ArrayList<String>();

    public void addProfile(NetworkInterface.NIC nic, String name) {
        if (this.name.contains(name)) {
            throw new IllegalArgumentException("Duplicate profile name");
        } else {
            NetworkInterface.NIC temp = new NetworkInterface.NIC();
            NetworkInterface.clone(temp, nic);
            this.nic.add(temp);
            this.name.add(name);
        }
    }

    public NetworkInterface.NIC getProfile(int index) {
        System.out.println(name.get(index));
        NetworkInterface.printNIC(nic.get(index));
        return nic.get(index);
    }

    public NetworkInterface.NIC getProfile(String name) {
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

    public static void saveProfileToFile(String fileName, NetworkInterface.NIC nic, String name) throws IOException {


        File file = new File(fileName);

        if (!file.exists()) {
            createFile(fileName);
        }

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
        if  (fileCreated) {
            PrintStream fileWriter = new PrintStream(new FileOutputStream(fileName, true));
            System.out.println("File created: " + fileName);
            if (header) {
                fileWriter.println("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
                fileWriter.println();
            }

            fileWriter.flush();
            fileWriter.close();
        } else throw new IOException("File already existing: " + fileName);
    }


    private static void createFile(String fileName) throws IOException {
        createFile(fileName, true);
    }

    public static class Profiles {
        public Profiles() {
        }

        private ArrayList<NetworkInterface.NIC> nic = new ArrayList<NetworkInterface.NIC>();
        private ArrayList<String> profileName = new ArrayList<String>();

        public void addNic(NetworkInterface.NIC nic, String profileName) {
            this.nic.add(nic);
            this.profileName.add(profileName);
        }

        public NetworkInterface.NIC getNic (int index) {
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

        File file = new File(fileName);

        if (!file.exists()) {
            createFile(fileName);
        }

        Scanner fileReader = new Scanner(file);

        Profiles profiles = new Profiles();
        NetworkInterface.NIC tempNic = new NetworkInterface.NIC();
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
            }
        }
        fileReader.close();
        return profiles;
    }

    public static void removeProfileFromFile(String fileName, int index) {

        File file = new File(fileName);
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File tempFile = new File("temp" + fileName);

        boolean readingProfile = false;
        int readingIndex = -1;
        int lineNumber = 0;

        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();

            if (line.contains("<profile>")) readingIndex++;

            if (!(readingIndex == index)) {
                try {
                    if (!tempFile.exists()) {
                        createFile(tempFile.getName(), false);
                    }
                } catch (IOException e) {

                }

                PrintStream fileWriter = null;
                try {
                    fileWriter = new PrintStream(new FileOutputStream(tempFile, true));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                fileWriter.println(line);
                fileWriter.close();
            }
        }

        fileReader.close();
        Path path = Paths.get(fileName);
        Path tempPath = Paths.get(tempFile.getName());
        try {
            Files.delete(path);
            Files.move(tempPath, tempPath.resolveSibling(fileName));
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
 }