package org.mlb.ProfileFileManager;

import java.io.*;

class CreateXmlFile<file> {

    public static File createXmlFile(String fileName) throws IOException {
        File file = new File(fileName);
        if  (file.createNewFile()) {
            printXmlHeaderToFile(file);
        } else throw new IOException("File already existing: " + fileName);
        return file;
    }

    private static void printXmlHeaderToFile(File file) {
        try {
            PrintStream fileWriter = new PrintStream(new FileOutputStream(file.getName(), true));
            fileWriter.println("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
            fileWriter.println();
            fileWriter.flush();
            fileWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
