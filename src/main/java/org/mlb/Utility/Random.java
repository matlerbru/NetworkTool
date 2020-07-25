package org.mlb.Utility;

public class Random {

    public static String getString(int lowestLength, int highestLength) {
        int length = getInt(lowestLength, highestLength);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)getInt(33, 126));
        }
       return sb.toString().trim();
    }

    public static int getInt(int lowestValue, int highestValue) {
        int span = highestValue - lowestValue + 1;
        int number = (int)(lowestValue + Math.random() * span);
        return number;
    }

    public static boolean getBoolean() {
        return Math.random() < 0.5;
    }

    public static String getNetworkAddress() {
        String address = getInt(0, 254) + "." + getInt(0, 254) + "." + getInt(0, 254) + "." + getInt(0, 254);
        return address;
    }

    public static String getMacAddress() {
        String address = getRandomBitInHex() + "-" + getRandomBitInHex() + "-" + getRandomBitInHex() + "-" + getRandomBitInHex()  + "-" + getRandomBitInHex() + "-" + getRandomBitInHex();
        return address;
    }

    private static String getRandomBitInHex() {
        String hex = Integer.toHexString(getInt(0, 255)).toUpperCase();
        if (hex.length() == 1) {
            hex = "0" + hex;
        }
        return hex;
    }
}