package org.mlb.Utility;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestFormatTest {

    @Test
    public void isFormattedAsIp() {
        assertEquals(false, TextFormat.isFormattedAsIp(""));

        assertEquals(false, TextFormat.isFormattedAsIp(null));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.100."));

        assertEquals(false, TextFormat.isFormattedAsIp(".100.100.100"));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.100"));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.256.100"));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.-1.100"));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.100..100"));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.100.100.100"));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.100-100"));

        assertEquals(false, TextFormat.isFormattedAsIp("100.100.100.100a"));

        assertEquals(false, TextFormat.isFormattedAsIp("b100.100.100.100"));

        assertEquals(true, TextFormat.isFormattedAsIp("100.100.100.100"));

        assertEquals(true, TextFormat.isFormattedAsIp("0.0.255.255"));

        String ip = Random.getNetworkAddress();
        assertEquals("Not farmatted as IP: \"" + ip + "\"", true, TextFormat.isFormattedAsIp(ip));
    }

    @Test
    public void isFormattedAsMac() {
        assertEquals(false, TextFormat.isFormattedAsMac(""));

        assertEquals(false, TextFormat.isFormattedAsMac(null));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA-AA-AA-"));

        assertEquals(false, TextFormat.isFormattedAsMac("-AA-AA-AA-AA-AA"));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA-AA-AA"));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA-AA.AA-AA"));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA-FG-AA-AA"));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA--AA-AA-AA"));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA-AA-AA-AA-AA"));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA-AA-AA-AAb"));

        assertEquals(false, TextFormat.isFormattedAsMac("bAA-AA-AA-AAAA-AA"));

        assertEquals(false, TextFormat.isFormattedAsMac("AA-AA-AA-AA-Af-AA"));

        assertEquals(true, TextFormat.isFormattedAsMac("AA-AA-AA-AA-AA-AA"));

        assertEquals(true, TextFormat.isFormattedAsMac("00-0A-A0-AA-B8-FF"));

        String mac = Random.getMacAddress();
        assertEquals("Not formatted as MAC: \"" + mac + "\"", true, TextFormat.isFormattedAsMac(mac));





    }
    
}