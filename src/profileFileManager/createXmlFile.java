package profileFileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

class createXmlFile {

    public static void createFile(String fileName, boolean includeHeader) throws IOException {
        File file = new File(fileName);
        boolean fileCreated = file.createNewFile();
        if  (fileCreated) {
            PrintStream fileWriter = new PrintStream(new FileOutputStream(fileName, true));
            if (includeHeader) {
                fileWriter.println("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>");
                fileWriter.println();
            }
            fileWriter.flush();
            fileWriter.close();
        } else throw new IOException("File already existing: " + fileName);
    }

    public static void createFile(String fileName) throws IOException {
        createFile(fileName, true);
    }
}
