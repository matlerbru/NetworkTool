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

    private ArrayList<NetworkInterfaceController> nic = new ArrayList<NetworkInterfaceController>();
    private ArrayList<String> name = new ArrayList<String>();

    public void addProfile(NetworkInterfaceController nic, String name) {
        if (this.name.contains(name)) {
            throw new IllegalArgumentException("Duplicate profile name");
        } else {
            NetworkInterfaceController temp = new NetworkInterfaceController();
            NetworkInterfaceController.clone(temp, nic);
            this.nic.add(temp);
            this.name.add(name);
        }
    }

    public NetworkInterfaceController getProfile(int index) {
        return nic.get(index);
    }

    public NetworkInterfaceController getProfile(String name) {
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

    public static class Profiles {
        public Profiles() {
        }

        private ArrayList<NetworkInterfaceController> nic = new ArrayList<NetworkInterfaceController>();
        private ArrayList<String> profileName = new ArrayList<String>();

        public void addNic(NetworkInterfaceController nic, String profileName) {
            this.nic.add(nic);
            this.profileName.add(profileName);
        }

        public NetworkInterfaceController getNic (int index) {
            return nic.get(index);
        }

        public String getProfileName (int index) {
            return profileName.get(index);
        }

        public int size () {
            return nic.size();
        }
    }
 }
