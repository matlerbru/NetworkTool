package org.mlb.Utility;

public class TextFormat {

    public static boolean isFormattedAsIp(String string) {
        try {
            for (int i = 0; i < 3; i++) {
                if (string.contains(".")) {
                    int dot = string.indexOf(".");
                    String temp = string.substring(0, dot);
                    int value = Integer.parseInt(temp);
                    if (value < 0 || value > 255) {
                        throw new Exception();
                    }
                    string = string.substring(dot + 1);
                } else {
                    throw new Exception();
                }
            }
            int value = Integer.parseInt(string);
            if (value < 0 || value > 255) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }   

    public static boolean isFormattedAsMac(String string) {

        try {
            for (int i = 0; i < 5; i++) {
                if (string.contains("-")) {
                    int hyphen = string.indexOf("-");
                    String hexValue = string.substring(0, hyphen);
                    int value = Integer.parseInt(hexValue, 16);
                    if (value < 0 || value > 255) {
                        throw new Exception();
                    }
                    if (!isOnlyUpperCase(hexValue)) throw new Exception();
                    if (hexValue.length() != 2) throw new Exception();
                    string = string.substring(hyphen + 1);
                } else {
                    throw new Exception();
                }
            }
            int value = Integer.parseInt(string, 16);
            if (value < 0 || value > 255) {
                throw new Exception();
            }
            if (!isOnlyUpperCase(string)) throw new Exception();
            return true;
        } catch (Exception e) {
            return false;
        }
    }    

    private static boolean isOnlyUpperCase(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) > 90) return false;
        }
        return true;
    }
    
}