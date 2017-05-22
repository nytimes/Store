package com.nytimes.android.external.fs3.filesystem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import okio.Buffer;
import okio.BufferedSource;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FSFileTest {
    private static final String TEST_FILE_PATH = "/test_file";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    File root;
    @Mock
    BufferedSource source;

    private FSFile fsFile;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        File root = folder.newFolder();
        fsFile = new FSFile(root, TEST_FILE_PATH);
    }


    @Test
    public void closeSourceAfterWrite() throws IOException {
        when(source.read(any(Buffer.class), anyByte())).thenReturn(Long.valueOf(-1));
        fsFile.write(source);
        verify(source).close();
    }
}
