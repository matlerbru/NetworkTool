package org.mlb.Utility;

import static org.junit.Assert.*;

import org.junit.*;

public class OccourancesOfTest {

    private String string = " abc ab123baab a b";

    @Test
    public void ordinalIndexOf() {

        assertEquals(4, OccourancesOf.occourancesOf(string, " "));
        assertEquals(1, OccourancesOf.occourancesOf(string, "123"));
        assertEquals(5, OccourancesOf.occourancesOf(string, "a"));
        assertEquals(1, OccourancesOf.occourancesOf(string, " b"));
        assertEquals(1, OccourancesOf.occourancesOf(string, " abc ab123baab a b"));
        assertEquals(0, OccourancesOf.occourancesOf(string, "g"));
        assertEquals(0, OccourancesOf.occourancesOf(string, " abc ab123baab a b "));
    }

    
}