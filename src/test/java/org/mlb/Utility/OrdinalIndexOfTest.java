package org.mlb.Utility;

import static org.junit.Assert.*;

import org.junit.*;

public class OrdinalIndexOfTest {
    
    private String string = "abc ab";

    @Test
    public void ordinalIndexOf() {

        assertEquals(-1, OrdinalIndexOf.ordinalIndexOf(string, "d", 1));
        assertEquals(2, OrdinalIndexOf.ordinalIndexOf(string, "c", 1));
        assertEquals(-1, OrdinalIndexOf.ordinalIndexOf(string, "a", 0));
        assertEquals(0, OrdinalIndexOf.ordinalIndexOf(string, "a", 1));
        assertEquals(4, OrdinalIndexOf.ordinalIndexOf(string, "a", 2));
    }


}