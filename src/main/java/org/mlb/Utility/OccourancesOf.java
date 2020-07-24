package org.mlb.Utility;

public class OccourancesOf {
    
    public static int occourancesOf(String string, String substring) {
        int occourances = 0;
        for (int i = 0; i + substring.length() <= string.length(); i++) {
            if (string.substring(i, i + substring.length()).equals(substring)) {
                occourances++;
            }
        }
        return occourances;
    }
}