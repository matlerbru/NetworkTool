package org.mlb.ProfileFileManager;

import static org.junit.Assert.*;

import java.io.*;

import org.junit.*;
import org.mlb.NetworkInterfaceTool.*;
import org.mlb.Utility.*;

public class RemoveProfileFromFileTest {

    private final int AMOUNT_OF_PROFILES_TO_CREATE = 10;
    private int amountOfProfilesToDelete;
    
    private String testFilePath = "src/test/resources/RemoveProfileFromFileTest.xml";
    private File file;

    private static Profiles writeProfiles = new Profiles();
    private static Profiles readProfiles = new Profiles();

    @Before
    public void createAndSaveToFile() {
        amountOfProfilesToDelete = Random.getInt(1, AMOUNT_OF_PROFILES_TO_CREATE);

        assertFalse("Failure deleting file", !deleteFileIfExisting());
        for (int i = 0; i < AMOUNT_OF_PROFILES_TO_CREATE; i++) {
            if (!createAndAddProfilesToFile()) {
                i--;
            }
        }
        
        for (int i = 0; i < amountOfProfilesToDelete; i++) {
            try {
                int index = Random.getInt(0, writeProfiles.getListOfKeys().size() - 1);
                new RemoveProfileFromFile().remove(testFilePath, writeProfiles.getListOfKeys().get(index));
                writeProfiles.removeProfile(writeProfiles.getListOfKeys().get(index));
            } catch (Exception e) {
                assertFalse("Unable to remove profiles from file", true);
            }
        }

        try {
            readProfiles = new LoadProfilesFromFile().load(testFilePath);    
        } catch (IOException e) {
            assertFalse("Unable to read profiles from file", true);
        }
    }

    private boolean deleteFileIfExisting() {
        file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
            if (file.exists()) return false;
        }
        return true;
    }

    public boolean createAndAddProfilesToFile() {
        try {
            String profileName = Random.getString(0, 20);
            writeProfiles.addProfile(NetworkInterfaceController.getRandomNic(), profileName);
            new SaveProfileToFile().save(testFilePath, writeProfiles.getProfile(profileName), profileName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    public void save() {
        assertEquals(AMOUNT_OF_PROFILES_TO_CREATE - amountOfProfilesToDelete, readProfiles.size());
        for (String key : writeProfiles.getListOfKeys()) {
            assertFalse("profileName found in write is not found in read: " + key, readProfiles.getProfile(key) == null);
            assertFalse("Nic profiles not equal", !NetworkInterfaceController.isEqual(writeProfiles.getProfile(key), readProfiles.getProfile(key)));
        }
    }

    @After
    public void removeFile() {
        file.delete();
    }

}