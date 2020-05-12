package NetworkTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadNetworkInterfaceControllerFromCmd {

    protected static NetworkInterfaceController get(int index) {
        ProcessBuilder pb = new ProcessBuilder();

        pb.command("cmd.exe", "/c", "ipconfig /all");
        int readIndex = 0;
        NetworkInterfaceController nic = new NetworkInterfaceController();

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            String lastLine = null;
            boolean newNIC = false;

            while ((line = reader.readLine()) != null) {
                if (line.length() == 0) {
                    try {
                        if (lastLine.contains(":") && !lastLine.contains(". .")) {
                            newNIC = true;
                            String displayName = lastLine.replace(":", "");
                            displayName = displayName.substring(displayName.indexOf("adapter") + 8);
                            nic.setDisplayName(displayName);
                        } else {
                            newNIC = false;
                        }
                    } catch (Exception e) {

                    }
                }

                if (newNIC) {

                    if (line.contains("Description . . . . . . . . . . . : ")) {
                        String name = line.replace("Description . . . . . . . . . . . : ", "");
                        name = name.substring(3);
                        nic.setName(name);
                    }

                    if (line.contains("Physical Address. . . . . . . . . : ")) {
                        String mac = line.replace("Physical Address. . . . . . . . . : ", "");
                        mac = mac.replace(" ", "");
                        nic.setMac(mac);
                    }

                    if (line.contains("IPv4 Address. . . . . . . . . . . : ")) {
                        String ip = line.replace("IPv4 Address. . . . . . . . . . . : ", "");
                        ip = ip.replace(" ", "");
                        ip = ip.replace("(Preferred)", "");
                        ip = ip.replace("(Tentative)", "");
                        nic.setIPaddress(ip);
                    }

                    if (line.contains("Subnet Mask . . . . . . . . . . . : ")) {
                        String subnetMask = line.replace("Subnet Mask . . . . . . . . . . . : ", "");
                        subnetMask = subnetMask.replace(" ", "");
                        nic.setSubnetMask(subnetMask);
                    }

                    if (line.contains("Default Gateway . . . . . . . . . : ")) {
                        String defaultGateway = line.replace("Default Gateway . . . . . . . . . : ", "");
                        defaultGateway = defaultGateway.replace(" ", "");
                        nic.setDefaultGateway(defaultGateway);
                    }

                    if (line.contains("DHCP Enabled. . . . . . . . . . . : ")) {
                        nic.setDhcp(line.contains("Yes"));
                    }

                } else {
                    try {
                        if (nic.getDisplayName().length() > 0) {
                            if (readIndex == index) {
                                return nic;
                            }
                            readIndex++;
                            nic = new NetworkInterfaceController(null, null, null, null, false, null, null);
                        }
                    } catch (Exception e) {

                    }
                }
                lastLine = line;
            }
            throw new IndexOutOfBoundsException("Index not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;






    }
}
