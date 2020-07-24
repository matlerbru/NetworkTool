package org.mlb.ProfileFileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mlb.Utility.OccourancesOf;
import org.mlb.Utility.OrdinalIndexOf;

public class RemoveProfileFromFile {

    private File file;
    private File tempFile;

    private Profiles profiles;

    public void remove(String fileName, String profileName) throws IOException {
        file = new File(fileName);

String formattedString = formatTempFileName(fileName);

        tempFile = new File(formattedString);
        profiles = new LoadProfilesFromFile().load(fileName);
        profiles.removeProfile(profileName);

        printProfilesToFile();
        
        useTempFileInsteadOfFile();
    }

    private String formatTempFileName(String fileName) {
        int amountOfSlash = OccourancesOf.occourancesOf(fileName, "/");
        int lastSlashIndex = OrdinalIndexOf.ordinalIndexOf(fileName, "/", amountOfSlash);
        return fileName.subSequence(0, lastSlashIndex + 1) + "temp" + fileName.substring(lastSlashIndex + 1);
    }    

    private void printProfilesToFile() throws IOException {
        for (String key : profiles.getListOfKeys()) {
            new SaveProfileToFile().save(tempFile.getPath(), profiles.getProfile(key), key);  
        }
        if (!tempFile.exists()) {
            try {
                tempFile = CreateXmlFile.createXmlFile(tempFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void useTempFileInsteadOfFile() throws IOException {
        Path path = Paths.get(file.getPath());
        Path tempPath = Paths.get(tempFile.getPath());
        try {
            Files.delete(path);
            Files.move(tempPath, tempPath.resolveSibling(file.getName()));
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        } catch (Exception e) {
            throw new IOException("Unable to move temporary file");
        }
    }

} //temp kommer på først, læg den efter sidste /