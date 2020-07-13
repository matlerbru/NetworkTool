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
        int lineNumber = -1;
        int profileIndex = -1;
        while (fileReader.hasNextLine()){
            String line = fileReader.nextLine();
            lineNumber++;
            if (line.contains("<profile>")) profileIndex++;
            if (!(profileIndex == index)) {
                try {
                    if (!tempFile.exists()) {
                        tempFile = CreateXmlFile.createXmlFile(tempFile.getName());
                    }
                } catch (IOException e) {
                }
                if (lineNumber > 1) {
                    try {
                        PrintStream fileWriter = new PrintStream(new FileOutputStream(tempFile, true));
                        fileWriter.println(line);
                        fileWriter.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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
