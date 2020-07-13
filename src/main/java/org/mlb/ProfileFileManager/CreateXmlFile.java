package org.mlb.ProfileFileManager;

import java.io.*;

class CreateXmlFile<file> {

    private File file;

    public CreateXmlFile(String fileName) throws IOException {
        createXmlFile(fileName);
    }

    private void createXmlFile(String fileName) throws IOException {
        file = new File(fileName);
        if  (file.createNewFile()) {
            printXmlHeaderToFile();
        } else throw new IOException("File already existing: " + fileName);
    }

    private void printXmlHeaderToFile() {
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

    public File getFile() {
        return file;
    }

}
