package com.nytimes.android.external.fs;

import com.google.common.collect.ImmutableMap;
import com.nytimes.android.external.fs.impl.BaseTestCase;
import com.nytimes.android.external.fs.impl.FileSystemImpl;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import okio.BufferedSource;
import okio.Okio;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.createTempDir;
import static java.util.Arrays.asList;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiTest extends BaseTestCase {

    private static final Map<String, List<String>> fileData
            = ImmutableMap.<String, List<String>>builder()
            .put("/foo/bar.txt", asList("sfvSFv", "AsfgasFgae", "szfvzsfbzdsfb"))
            .put("/foo/bar/baz.xyz", asList("sasffvSFv", "AsfgsdvzsfbvasFgae", "szfvzsfszfvzsvbzdsfb"))
            .build();

    private FileSystem createAndPopulateTestFileSystem() throws IOException {
        File baseDir = createTempDir();
        FileSystem fileSystem = new FileSystemImpl(baseDir);
        for (String path : fileData.keySet()) {
            for (String data : fileData.get(path)) {
                BufferedSource source = source(data);
                fileSystem.write(path, source);
                source.close();
            }
        }
        assertThat(fileSystem.list("/").size()).isEqualTo(fileData.size());
        return fileSystem;
    }

    @Test
    public void deleteAll() throws IOException {
        FileSystem fileSystem = createAndPopulateTestFileSystem();
        fileSystem.deleteAll("/");
        assertThat(fileSystem.list("/").size()).isZero();
    }

    @Test
    public void listNCompare() throws IOException {
        FileSystem fileSystem = createAndPopulateTestFileSystem();
        int assertCount = 0;
        for (String path : fileSystem.list("/")) {
            String data = fileSystem.read(path).readUtf8();
            List<String> written = fileData.get(path);
            String writtenData = written.get(written.size() - 1);
            assertThat(data).isEqualTo(writtenData);
            assertCount++;
        }
        assertThat(assertCount).isEqualTo(fileData.size());
    }

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }
}
