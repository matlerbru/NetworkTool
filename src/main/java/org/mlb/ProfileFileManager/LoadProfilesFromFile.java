package org.mlb.ProfileFileManager;

import org.mlb.NetworkTool.NetworkInterfaceController;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LoadProfilesFromFile {

    public static void load(String fileName) throws IOException {

        File file = new File(fileName);

        if (!file.exists()) {
            new CreateXmlFile(fileName);
        }

        Scanner fileReader = new Scanner(file);

        NetworkInterfaceController tempNic = new NetworkInterfaceController();
        String tempProfileName = new String("");
        boolean readingProfile = false;

        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            if (line.contains("<profile>")) {
                readingProfile = true;
            } else if (line.contains("</profile>")) {
                readingProfile = false;
                ProfileContainer.container.addProfile(tempNic, tempProfileName);
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
    }

}
