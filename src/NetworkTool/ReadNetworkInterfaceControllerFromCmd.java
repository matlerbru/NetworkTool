package NetworkTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class ReadNetworkInterfaceControllerFromCmd {

    private NetworkInterfaceController nic = new NetworkInterfaceController();

    private String lineBeingProcessed;

    private String previousLine;

    private boolean newNIC;

    private BufferedReader textStream;

    protected NetworkInterfaceController get(int index) {
        int readIndex = 0;
        receiveTextStreamFromCmd();
        while ((lineBeingProcessed = getNextLineFromStream()) != null) {
            determineIfNewNic();
            if (newNIC) {
                extractNetworkInterfaceController();
            } else if (nic.getDisplayName() != null) {
                if (readIndex == index) {
                    return nic;
                }
                readIndex++;
                nic = new NetworkInterfaceController();
            }
        }
        throw new IndexOutOfBoundsException("Index not found");
    }

    private void receiveTextStreamFromCmd() {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("cmd.exe", "/c", "ipconfig /all");
        try {
            Process process = pb.start();
            textStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNextLineFromStream() {
        try {
            return textStream.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    private void determineIfNewNic() {
        try {
            if (lineBeingProcessed.length() == 0) {
                if (previousLine.contains(":") && !previousLine.contains(". .")) {
                    newNIC = true;
                    String displayName = previousLine.replace(":", "");
                    displayName = displayName.substring(displayName.indexOf("adapter") + 8);
                    nic.setDisplayName(displayName);
                } else {
                    newNIC = false;
                }
            }
        } catch (Exception e) {
        } finally {
            previousLine = lineBeingProcessed;
        }
    }

    private void extractNetworkInterfaceController() {
        extractNicName();
        extractNicMac();
        extractNicIp();
        extractNicSubnetMask();
        extractNicDefaultGateway();
        extractNicDhcpEnabled();
    }

    private void extractNicDhcpEnabled() {
        if (lineBeingProcessed.contains("DHCP Enabled. . . . . . . . . . . : ")) {
            nic.setDhcp(lineBeingProcessed.contains("Yes"));
        }
    }

    private void extractNicDefaultGateway() {
        if (lineBeingProcessed.contains("Default Gateway . . . . . . . . . : ")) {
            String defaultGateway = lineBeingProcessed.replace("Default Gateway . . . . . . . . . : ", "");
            defaultGateway = defaultGateway.replace(" ", "");
            nic.setDefaultGateway(defaultGateway);
        }
    }

    private void extractNicSubnetMask() {
        if (lineBeingProcessed.contains("Subnet Mask . . . . . . . . . . . : ")) {
            String subnetMask = lineBeingProcessed.replace("Subnet Mask . . . . . . . . . . . : ", "");
            subnetMask = subnetMask.replace(" ", "");
            nic.setSubnetMask(subnetMask);
        }
    }

    private void extractNicIp() {
        if (lineBeingProcessed.contains("IPv4 Address. . . . . . . . . . . : ")) {
            String ip = lineBeingProcessed.replace("IPv4 Address. . . . . . . . . . . : ", "");
            ip = ip.replace(" ", "");
            ip = ip.replace("(Preferred)", "");
            ip = ip.replace("(Tentative)", "");
            nic.setIPaddress(ip);
        }
    }

    private void extractNicMac() {
        if (lineBeingProcessed.contains("Physical Address. . . . . . . . . : ")) {
            String mac = lineBeingProcessed.replace("Physical Address. . . . . . . . . : ", "");
            mac = mac.replace(" ", "");
            nic.setMac(mac);
        }
    }

    private void extractNicName() {
        if (lineBeingProcessed.contains("Description . . . . . . . . . . . : ")) {
            String name = lineBeingProcessed.replace("Description . . . . . . . . . . . : ", "");
            name = name.substring(3);
            nic.setName(name);
        }
    }
}
