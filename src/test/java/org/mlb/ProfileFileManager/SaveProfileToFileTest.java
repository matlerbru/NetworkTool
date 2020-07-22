package org.mlb.ProfileFileManager;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;
import org.mlb.NetworkInterfaceTool.*;
import org.mlb.Utility.*;

public class SaveProfileToFileTest {

    private final int AMOUNT_OF_PROFILES_TO_TEST = 10;

    private String testFilePath = "src/test/resources/SaveProfileToFileTest.xml";
    private File file;

    private static Profiles writeProfiles = new Profiles();
    private static Profiles readProfiles = new Profiles();

    @Before
    public void checkNoFile() {
        assertFalse("Failure creating file", !createFile());
        for (int i = 0; i < AMOUNT_OF_PROFILES_TO_TEST; i++) {
            if (!createAndAddProfilesToFile()) {
                i--;
            }
        }
        for (int i = 0; i < AMOUNT_OF_PROFILES_TO_TEST; i++) {
            try {
                readProfiles = LoadProfilesFromFile.load(testFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean createFile() {
        file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
            if (file.exists()) return false;
        }
        try {
            CreateXmlFile.createXmlFile(testFilePath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean createAndAddProfilesToFile() {
        try {
            String profileName = Random.getString(0, 20);
            writeProfiles.addProfile(NetworkInterfaceController.getRandomNic(), profileName);
            SaveProfileToFile.save(testFilePath, writeProfiles.getProfile(profileName), profileName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    public void save() {
        assertEquals(AMOUNT_OF_PROFILES_TO_TEST, readProfiles.size());
        for (String key : writeProfiles.getListOfKeys()) {
            assertFalse(!NetworkInterfaceController.isEqual(writeProfiles.getProfile(key), readProfiles.getProfile(key)));
        }
    }

    @After
    public void removeFile() {
        file.delete();
    }
}    
