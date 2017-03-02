package com.nytimes.android.external.fs.filesystem;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class BreadthFirstFileTreeIteratorTest {

    private File systemTempDir;

    @Before
    public void setUp() throws IOException {
        String property = "java.io.tmpdir";
        systemTempDir = createDirWithSubFiles(new File(System.getProperty(property)), 0);
    }

    @Test
    public void testHasNextEmpty() throws IOException {
        File hasNextDir = createDirWithSubFiles(systemTempDir, 0);
        BreadthFirstFileTreeIterator btfti = new BreadthFirstFileTreeIterator(hasNextDir);
        assertThat(btfti.hasNext()).isFalse();
    }

    @Test
    public void testHasNextOne() throws IOException {
        File hasNextDir = createDirWithSubFiles(systemTempDir, 1);
        BreadthFirstFileTreeIterator btfti = new BreadthFirstFileTreeIterator(hasNextDir);
        assertThat(btfti.hasNext()).isTrue();
        assertThat(btfti.next()).isNotNull();
        assertThat(btfti.hasNext()).isFalse();
    }

    @Test
    public void testHastNextMany() throws IOException {
        int fileCount = 30;
        File hasNextDir = createDirWithSubFiles(systemTempDir, fileCount);
        createDirWithSubFiles(hasNextDir, fileCount);
        BreadthFirstFileTreeIterator btfti = new BreadthFirstFileTreeIterator(hasNextDir);
        int counter = 0;
        while (btfti.hasNext()) {
            btfti.next();
            counter++;
        }
        assertThat(counter).isEqualTo(fileCount * 2);
    }

    private File createDirWithSubFiles(File root, int fileCount) throws IOException {
        assertThat(root).exists();
        assertThat(root.isDirectory()).isTrue();

        File tempDir = createDir(root);
        for (int i = 0; i < fileCount; i++) {
            createFile(tempDir);
        }
        return tempDir;
    }

    private void createFile(File root) throws IOException {
        File someFile = new File(root, "somefile" + System.nanoTime());
        assertThat(someFile.createNewFile()).isTrue();
        someFile.deleteOnExit();
    }

    private File createDir(File root) {
        String label = "BFTI_test_" + System.nanoTime();
        File tempDir = new File(root, label);

        assertThat(tempDir.mkdir()).isTrue();
        assertThat(tempDir.exists()).isTrue();
        assertThat(tempDir.isDirectory()).isTrue();
        tempDir.deleteOnExit();
        return tempDir;
    }
}
