package profileFileManager;

import NetworkTool.NetworkInterfaceController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class SaveProfileToFile {

    public static void save(String fileName, NetworkInterfaceController nic, String name) {
        try {
            File file = new File(fileName);

            if (!file.exists()) {
                new CreateXmlFile(fileName);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
