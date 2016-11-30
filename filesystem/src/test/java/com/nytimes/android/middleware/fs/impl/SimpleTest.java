package com.nytimes.android.middleware.fs.impl;


import com.nytimes.android.middleware.fs.FileSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSource;
import okio.Okio;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.createTempDir;

public class SimpleTest extends BaseTestCase {

    private static final String testString1 = "aszfbW#$%#$^&*5 r7ytjdfbv!@#R$\n@!#$%2354 wtyebfsdv\n";
    private static final String testString2 = "#%^sdfvb#W%EtsdfbSER@#$%dsfb\nASRG \n #dsfvb \n";

    private static FileSystem fileSystem;

    @Before
    public void start() throws IOException {
        File baseDir = createTempDir();
        fileSystem = new FileSystemImpl(baseDir);
    }

    @Test(expected = FileNotFoundException.class)
    public void loadFileNotFound() throws IOException {
        fileSystem.read("/loadFileNotFound.txt").readUtf8();
    }

    @Test
    public void saveNload() throws IOException {
        diffMe("/flibber.txt", "/flibber.txt");
        diffMe("/blarg/flibber.txt", "/blarg/flibber.txt");
        diffMe("/blubber.txt", "blubber.txt");
        diffMe("/blarg/blubber.txt", "blarg/blubber.txt");
    }

    @Test
    public void delete() throws IOException {
        fileSystem.write("/boo", source(testString1));
        assertEquals(testString1, fileSystem.read("/boo").readUtf8());
        fileSystem.delete("/boo");
        assertFalse(fileSystem.exists("/boo"));
    }

    @Test
    public void deleteWhileReading() throws IOException {

        fileSystem.write("/boo", source(testString1));
        BufferedSource source = fileSystem.read("/boo");
        fileSystem.delete("/boo");

        assertFalse(fileSystem.exists("/boo"));
        assertEquals(testString1, source.readUtf8());
        assertFalse(fileSystem.exists("/boo"));
    }

    @Test
    public void deleteWhileReadingThenWrite() throws IOException {

        fileSystem.write("/boo", source(testString1));


        BufferedSource source1 = fileSystem.read("/boo"); // open a source and hang onto it
        fileSystem.delete("/boo"); // now delete the file

        assertFalse(fileSystem.exists("/boo")); // exists() should say it's gone even though
                                                // we still have a source to it
        fileSystem.write("/boo", source(testString2)); // and now un-delete it by writing a new version
        assertTrue(fileSystem.exists("/boo")); // exists() should say it's back
        BufferedSource source2 = fileSystem.read("/boo"); // open another source and hang onto it
        fileSystem.delete("/boo"); // now delete the file *again*

        // the sources should have the correct data even though the file was deleted/re-written/deleted
        assertEquals(testString1, source1.readUtf8());
        assertEquals(testString2, source2.readUtf8());

        // now that the 2 sources have been fully read, you shouldn't be able to read it
        assertFalse(fileSystem.exists("/boo"));
    }

    private void diffMe(String first, String second) {
        try {
            fileSystem.write(first, source(testString1));
        } catch (IOException error) {
            throw new RuntimeException("unable to write to " + first, error);
        }

        try {
            assertEquals(testString1, fileSystem.read(second).readUtf8());
        } catch (IOException error) {
            throw new RuntimeException("unable to read from " + second, error);
        }

        try {
            fileSystem.write(second, source(testString2));
        } catch (IOException error) {
            throw new RuntimeException("unable to write to " + second, error);
        }

        try {
            assertEquals(testString2, fileSystem.read(first).readUtf8());
        } catch (IOException error) {
            throw new RuntimeException("unable to read from " + first, error);
        }
    }

    @After
    public void stop() {
        // Write some after test execution steps.
        //Added above to remove PMD violations.
    }

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }

}
