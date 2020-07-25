package org.mlb.Utility;

import org.junit.Test;

import static org.junit.Assert.*;

public class RandomTest {
    
    @Test
    public void getString() {
        assertFalse("getString() length not in range", Random.getString(0,0).length() != 0);

        assertFalse("getString() length not in range", Random.getString(20,20).length() != 20);

        String string = Random.getString(100, 100);
        for(int i = 0; i < string.length(); i++) {
            assertFalse("getString() illegal string character", string.charAt(i) < 33 || string.charAt(i) > 126);
        }
    }

    @Test
    public void getInt() {
        assertFalse("getint() out of range", Random.getInt(0, 0) != 0);

        assertFalse("getint() out of range", Random.getInt(100, 100) != 100);

        for (int i=0; i < 1000; i++) {
            int integer = Random.getInt(0, 100);
            assertFalse("getint() out of range", integer < 0 || integer > 100);
            integer = Random.getInt(-100, 0);
            assertFalse("getint() out of range", integer < -100 || integer > 0);
        }
    }

    @Test
    public void getNetworkAddress() {
        for (int i = 0; i < 1000; i++) {
            String networkAddress = Random.getNetworkAddress();
            assertFalse("getNetworkAddress() is not formatted as IPv4 address: \"" + networkAddress + "\"", !TextFormat.isFormattedAsIp(networkAddress));
        }
    }

    @Test
    public void getMacAddress() {
        for (int i = 0; i < 1000; i++) {
            String macAddress = Random.getMacAddress();
            assertFalse("getMacAddress() is not formatted as mac address: \"" + macAddress + "\"", !TextFormat.isFormattedAsMac(macAddress));
        }

    }

}