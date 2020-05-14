package profileFileManager;

import java.io.*;

class createXmlFile {

    public static void createFile(String fileName, boolean includeHeader) throws IOException {
        File file = new File(fileName);
        if  (file.createNewFile()) {
            if (includeHeader) {
                printXmlHeaderToFile(file);
            }
        } else throw new IOException("File already existing: " + fileName);
    }

    public static void createFile(String fileName) throws IOException {
        createFile(fileName, true);
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
