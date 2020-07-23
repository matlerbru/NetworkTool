package org.mlb.ProfileFileManager;

import org.mlb.NetworkInterfaceTool.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LoadProfilesFromFile {

    private File file;
    private Profiles nicProfiles = new Profiles();
    private Scanner fileReader;
    private String line;

    public Profiles load(String fileName) throws IOException {
        file = new File(fileName);
        createFileIfNotExisting();
        fileReader = new Scanner(file);

        extractAllProfiles();

        fileReader.close();
        return nicProfiles;
    }

    private void createFileIfNotExisting() throws IOException {
        if (!file.exists()) {
            file = CreateXmlFile.createXmlFile(file.getName());
            if (!file.exists()) {
                throw new IOException("Unable to create file: " + file.getName());
            }
        }
    }

    private void extractAllProfiles() {
        while (fileReader.hasNextLine()){
            line = fileReader.nextLine();
            if (line.contains("<profile>")) {
                extractNic();
            }    
        }
    }

    private void extractNic() {
        String profileName = null;
        NetworkInterfaceController nic = new NetworkInterfaceController();

        while (fileReader.hasNextLine()){    
            line = fileReader.nextLine();
            if (line.contains("<profileName>")) {
                profileName = extractXmlSubchild("profileName");
            } else if (line.contains("<name>")) {
                nic.setName(extractXmlSubchild("name"));
            } else if (line.contains("<displayName>")) {
                nic.setDisplayName(extractXmlSubchild("displayName"));
            } else if (line.contains("<DHCP>")) {
                nic.setDhcp(extractXmlSubchild("DHCP").contains("true"));
            } else if (line.contains("<IP>")) {
                nic.setIPaddress(extractXmlSubchild("IP"));
            } else if (line.contains("<subnetMask>")) {
                nic.setSubnetMask(extractXmlSubchild("subnetMask"));
            } else if (line.contains("<defaultGateway>")) {
                nic.setDefaultGateway(extractXmlSubchild("defaultGateway"));
            } else if (line.contains("</profile>")) {
                nicProfiles.addProfile(nic, profileName);
                break;
            }
        }
    }

    private String extractXmlSubchild(String subchild) {
        return line.trim().replace("<" + subchild + ">", "").replace("</" + subchild + ">", "");
    }

}
