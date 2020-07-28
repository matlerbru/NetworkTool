package org.mlb.Utility;

public class TextFormat {

    public static boolean isFormattedAsIp(String string) {
        return checkFormat(string, 4, ".", 10);
    }   

    public static boolean isFormattedAsMac(String string) {
        return checkFormat(string, 6, "-", 16) && string.length() == 17;
    }    

    private static boolean checkFormat(String string, int segments, String segmentSeperator, int base) {
        try {
            for (int i = 0; i < segments - 1; i++) {
                if (string.contains(segmentSeperator)) {
                    int seperatorIndex = string.indexOf(segmentSeperator);
                    String value = string.substring(0, seperatorIndex);
                    inRange(Integer.parseInt(value, base));
                    OnlyUpperCase(string);
                    string = string.substring(seperatorIndex + 1);
                } else {
                    throw new Exception();
                }

            }
            inRange(Integer.parseInt(string, base));
            OnlyUpperCase(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    
    }

    private static void inRange(int value) throws Exception {
        if (value < 0 || value > 255) {
            throw new Exception();
        }
    }

    private static void OnlyUpperCase(String string) throws Exception {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) > 90) throw new Exception();
        }
    }

}