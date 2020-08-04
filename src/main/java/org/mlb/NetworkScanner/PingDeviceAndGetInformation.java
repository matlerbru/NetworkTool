package org.mlb.NetworkScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.mlb.NetworkInterfaceTool.*;
import org.mlb.Utility.*;

class PingDeviceAndGetInformation {
    
    protected NetworkLocation start(String address, int timeout) throws IllegalStateException {
        String hostName = getHostNameFromIp(address, timeout);
        if (hostName != null) {
            String macAddr = getMacFromArpTable(address);
            String manufacturer = TextFormat.isFormattedAsMac(macAddr) ? getManufacturer(macAddr) : "NA";
            return new NetworkLocation(hostName, address, macAddr, manufacturer); 
        }
        return null;
    }
    
    private String getHostNameFromIp(String ipAddr, int timeout) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("cmd.exe", "/c", "ping -a -n 1 " + ipAddr + " -w " + timeout);
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Request timed out.")){
                    return null;
                }
                if (line.contains("[" + ipAddr + "]")) {
                    return line.substring(8, line.indexOf("[") - 1);
                } 
            }
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "NA";
    }

    private String getMacFromArpTable(String ipAddr) {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("cmd.exe", "/c", "arp -a");
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(ipAddr)) {      
                    return line.substring(24, 41).toUpperCase();
                } 
            }
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NA";
    }

    private String getManufacturer(String macAddr) {
        try {
            String mac = macAddr.replace("-", ":").substring(0, 8);
            File file = new File("src/main/resources/MacAddress.xml");
            Scanner scanner = new Scanner(file);

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(mac)) {
                    scanner.close();
                    int startString = OrdinalIndexOf.ordinalIndexOf(line, "\"", 3) + 1;
                    int endString = OrdinalIndexOf.ordinalIndexOf(line, "\"", 4);
                    return line.substring(startString, endString);
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "NA";
    }

}