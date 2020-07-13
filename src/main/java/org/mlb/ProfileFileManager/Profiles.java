package org.mlb.ProfileFileManager;

import org.mlb.NetworkInterfaceTool.*;

import java.util.*;

public class Profiles {

    public Profiles() {
    }

    private HashMap<String, NetworkInterfaceController> container = new HashMap<>();

    public void addProfile(NetworkInterfaceController nic, String profileName) {
        if (container.get(profileName) != null) {
            throw new IllegalArgumentException("Duplicate profile name");
        } else {
            NetworkInterfaceController temp = new NetworkInterfaceController();
            NetworkInterfaceController.clone(temp, nic);
            container.put(profileName, temp);
        }
    }

    public NetworkInterfaceController getProfile(String profileName) {
        return container.get(profileName);
    }

    public void removeProfile (String profileName) {
        container.remove(profileName);
    }


    public int size() {
        return container.size();
    }

    public List<String> getListOfKeys() {
        return new ArrayList<>(container.keySet());
    }

 }
