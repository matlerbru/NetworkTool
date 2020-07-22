package org.mlb.ProfileFileManager;

import static org.junit.Assert.*;

import java.io.*;
import java.util.Scanner;

import org.junit.*;

public class CreateXmlFileTest {

    private String testFilePath = "src/test/resources/CreateXmlFileTest.xml";
    private File file;
    private Scanner fileReader;
    
    @Before
    public void checkNoFile() {
        file = new File(testFilePath);
        if (file.exists()) {
            file.delete();
            assertFalse("Test file exists and can not be deleted", file.exists());
        }
        try {
            CreateXmlFile.createXmlFile(testFilePath);
            fileReader = new Scanner(file);
        } catch (IOException e) {
            assertFalse("IOexception", true);
        }
    }

    @Test
    public void createXmlFile() {
        assertEquals(true, file.exists());

        assertFalse("Created file contains no header", !fileReader.nextLine().contains("xml version="));

        assertFalse("Unwanted content in created file", !fileReader.nextLine().equals(""));

        assertEquals("Created file has too many lines", false, fileReader.hasNextLine());
    }

    @After
    public void removeFile() {
        fileReader.close();
        file.delete();        
    }
}    
