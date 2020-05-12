package NetworkTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class NetworkInterface {

    public static ArrayList<Nic> NIC = new ArrayList<Nic>();

    public static class Nic {
        public Nic() {

        }

        public Nic(String name, String displayName, String MAC, String IPaddress, boolean dhcp, String subnetMask, String defaultGateway) {
            this.name = name;
            this.displayName = displayName;
            this.MAC = MAC;
            this.IPaddress = IPaddress;
            this.dhcp = dhcp;
            this.subnetMask = subnetMask;
            this.defaultGateway = defaultGateway;
        }

        private String name;
        private String displayName;

        private String MAC;

        private String IPaddress;

        private boolean dhcp;

        private String subnetMask;
        private String defaultGateway;

        public String getName() {
            return name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getMAC() {
            return MAC;
        }

        public String getIPaddress() {
            return IPaddress;
        }

        public void setIPaddress(String IPaddress) {
            this.IPaddress = IPaddress;
        }

        public boolean isDhcp() {
            return dhcp;
        }

        public void setDhcp(boolean dhcp) {
            this.dhcp = dhcp;
        }

        public String getSubnetMask() {
            return subnetMask;
        }

        public void setSubnetMask(String subnetMask) {
            this.subnetMask = subnetMask;
        }

        public String getDefaultGateway() {
            return defaultGateway;
        }

        public void setDefaultGateway(String defaultGateway) {
            this.defaultGateway = defaultGateway;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void printNIC(Nic nic) {
            System.out.println("Display name: " + this.displayName);
            System.out.println("Name: " + this.name);
            System.out.println("DHCP: " + this.dhcp);
            System.out.println("MAC address: " + this.MAC);
            System.out.println("IP address: " + this.IPaddress);
            System.out.println("Subnet mask: " + this.subnetMask);
            System.out.println("Default gateway: " + this.defaultGateway);
            System.out.println();
        }
    }

    public static ArrayList<Nic> getNic () {
        return NIC;
    }

    public static void updateAllNic() {
        NIC.clear();
        int index = 0;
        while (true) {
            try {
                Nic nic = readNic(index);
                NIC.add(index, nic);
                index++;
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public static void updateNic(int index) {
        System.out.println("Index: " + index);
        Nic nic = readNic(index);
        NIC.remove(index);
        NIC.add(index, nic);
    }

    public static Nic readNic(int index) {
        ProcessBuilder pb = new ProcessBuilder();

        pb.command("cmd.exe", "/c", "ipconfig /all");
        int readIndex = 0;
        Nic nic = new Nic();

        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null;
            String lastLine = null;
            boolean newNIC = false;

            while ((line = reader.readLine()) != null) {
                if (line.length() == 0) {
                    try {
                        if (lastLine.contains(":") && !lastLine.contains(". .")) {
                            newNIC = true;
                            nic.displayName = lastLine.replace(":", "");
                            nic.displayName = nic.displayName.substring( nic.displayName.indexOf("adapter") + 8);
                        } else {
                            newNIC = false;
                        }
                    } catch (Exception e) {

                    }
                }

                if (newNIC) {

                    if (line.contains("Description . . . . . . . . . . . : ")) {
                        nic.name = line.replace("Description . . . . . . . . . . . : ", "");
                        nic.name = nic.name.substring(3, nic.name.length());
                    }

                    if (line.contains("Physical Address. . . . . . . . . : ")) {
                        nic.MAC = line.replace("Physical Address. . . . . . . . . : ", "");
                        nic.MAC = nic.MAC.replace(" ", "");
                    }

                    if (line.contains("IPv4 Address. . . . . . . . . . . : ")) {
                        nic.IPaddress = line.replace("IPv4 Address. . . . . . . . . . . : ", "");
                        nic.IPaddress = nic.IPaddress.replace(" ", "");
                        nic.IPaddress = nic.IPaddress.replace("(Preferred)", "");
                        nic.IPaddress = nic.IPaddress.replace("(Tentative)", "");
                    }

                    if (line.contains("Subnet Mask . . . . . . . . . . . : ")) {
                        nic.subnetMask = line.replace("Subnet Mask . . . . . . . . . . . : ", "");
                        nic.subnetMask = nic.subnetMask.replace(" ", "");
                    }

                    if (line.contains("Default Gateway . . . . . . . . . : ")) {
                        nic.defaultGateway = line.replace("Default Gateway . . . . . . . . . : ", "");
                        nic.defaultGateway = nic.defaultGateway.replace(" ", "");
                    }

                    if (line.contains("DHCP Enabled. . . . . . . . . . . : ")) {
                        nic.dhcp = (line.contains("Yes"));
                    }

                } else {
                    try {
                        if (nic.displayName.length() > 0) {
                            if (readIndex == index) {
                                return nic;
                            }
                            readIndex++;
                            nic = new Nic(null, null, null, null, false, null, null);
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


    public static void printNIC(Nic nic) {
        System.out.println("Display name: " + nic.getDisplayName());
        System.out.println("Name: " + nic.getName());
        System.out.println("DHCP: " + nic.isDhcp());
        System.out.println("MAC address: " + nic.getMAC());
        System.out.println("IP address: " + nic.getIPaddress());
        System.out.println("Subnet mask: " + nic.getSubnetMask());
        System.out.println("Default gateway: " + nic.getDefaultGateway());
        System.out.println();
    }

    public static void printNIC(int index) {
        printNIC(NIC.get(index));
    }

    public static void printNIC() {
        for (int index = 0; index < NIC.size(); index++) {
            printNIC(index);
        }
    }

    public static void pushNIC (Nic nic, int index) throws IllegalStateException {
        String name = NIC.get(index).getDisplayName();

        String IpCommand = "netsh interface ipv4 set address name=\"" + name + "\"";
        String NameCommand = "netsh interface set interface name=\"" + name + "\" newname=\"" + nic.getDisplayName() + "\"";

        System.out.println(NameCommand);

        if (nic.isDhcp()) {
            IpCommand = IpCommand + " dhcp";
        } else {
            IpCommand = IpCommand + " static " + nic.getIPaddress() + " " + nic.getSubnetMask() + " " + nic.getDefaultGateway();
        }

        try {
            System.out.println(IpCommand);
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("cmd.exe", "/c", IpCommand);
            Process processIp = pb.start();
            pb.command("cmd.exe", "/c", NameCommand);
            Process processName = pb.start();

            BufferedReader readerIp = new BufferedReader(new InputStreamReader(processIp.getInputStream()));

            String line;

            System.out.println();
            while ((line = readerIp.readLine()) != null) {
                System.out.println(line);
                if (line.length() > 0 && !line.contains("DHCP is already enabled on this interface.")) {
                    System.out.println("Throw");
                    throw new IllegalStateException(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clone(Nic destination, Nic source) {
        destination.name = source.name;
        destination.displayName = source.displayName;
        destination.dhcp = source.dhcp;
        destination.MAC = source.MAC;
        destination.IPaddress = source.IPaddress;
        destination.subnetMask = source.subnetMask;
        destination.defaultGateway = source.defaultGateway;
    }
}





























