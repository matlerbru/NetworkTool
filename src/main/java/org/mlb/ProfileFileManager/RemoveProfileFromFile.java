package org.mlb.ProfileFileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class RemoveProfileFromFile {

    public static void remove(String fileName, int index) {
        File file = new File(fileName);
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        File tempFile = new File("temp" + fileName);

        boolean readingProfile = false;
        int readingIndex = -1;
        int lineNumber = 0;

        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            if (line.contains("<profile>")) readingIndex++;
            if (!(readingIndex == index)) {
                try {
                    if (!tempFile.exists()) {
                        new CreateXmlFile(tempFile.getName(), false);
                    }
                } catch (IOException e) {
                }
                PrintStream fileWriter = null;
                try {
                    fileWriter = new PrintStream(new FileOutputStream(tempFile, true));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                fileWriter.println(line);
                fileWriter.close();
            }
        }
        fileReader.close();
        Path path = Paths.get(fileName);
        Path tempPath = Paths.get(tempFile.getName());
        try {
            Files.delete(path);
            Files.move(tempPath, tempPath.resolveSibling(fileName));
            Files.setAttribute(path, "dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
