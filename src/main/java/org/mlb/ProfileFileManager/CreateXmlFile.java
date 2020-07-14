package org.mlb.ProfileFileManager;

import java.io.*;

class CreateXmlFile<file> {

    public static File createXmlFile(String fileName) throws IOException {
        createFileAndPrintHeader(fileName);
        return new File(fileName);
    }

    private static void createFileAndPrintHeader(String file) {
        try {
            PrintStream fileWriter = new PrintStream(new FileOutputStream(file));
            fileWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileWriter.println();
            fileWriter.flush();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
