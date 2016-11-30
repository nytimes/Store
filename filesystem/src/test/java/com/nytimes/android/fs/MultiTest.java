package com.nytimes.android.fs;

import com.google.common.collect.ImmutableMap;
import com.nytimes.android.fs.impl.BaseTestCase;
import com.nytimes.android.fs.impl.FileSystemImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.createTempDir;
import static java.util.Arrays.asList;

public class MultiTest extends BaseTestCase {

    private static final Map<String, List<String>> fileData
            = ImmutableMap.<String, List<String>>builder()
            .put("/foo/bar.txt", asList("sfvSFv", "AsfgasFgae", "szfvzsfbzdsfb"))
            .put("/foo/bar/baz.xyz", asList("sasffvSFv", "AsfgsdvzsfbvasFgae", "szfvzsfszfvzsvbzdsfb"))
            .build();


    private static FileSystem fileSystem;

    @Before
    public void start() throws IOException {
        File baseDir = createTempDir();

        fileSystem = new FileSystemImpl(baseDir);
        for (String path : fileData.keySet()) {
            for (String data : fileData.get(path)) {
                fileSystem.write(path, source(data));
            }
        }
    }



    @Test
    public void deleteAll() throws IOException {
        fileSystem.deleteAll("/");
        assertEquals(0, fileSystem.list("/").size());

    }

    @Test
    public void listNCompare() throws IOException {
        Map<String, List<String>> listData = new HashMap<>();
        for (String path : fileSystem.list("/")) {
            String data = fileSystem.read(path).readUtf8();
            List<String> written = fileData.get(path);
            assertEquals(data, written.get(written.size() - 1));
        }
    }


    @After
    public void stop() {

    }

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }

}
