package org.mlb.Utility;

public class OrdinalIndexOf {
    public static int ordinalIndexOf(String string, String substring, int n) {
        try {
            if (n < 1) {
                throw new IllegalArgumentException();
            }
            int position = string.indexOf(substring);
            while (--n > 0 && position != -1)
                position = string.indexOf(substring, position + 1);
            return position;
        } catch (Exception e) {
            return -1;
        }
    }
}