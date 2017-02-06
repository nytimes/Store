package com.nytimes.android.external.fs;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import okio.BufferedSource;
import okio.Okio;

import static com.google.common.base.Charsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UtilTest {

    private final Util util = new Util();

    @Test
    public void testSimplifyPath() {
        assertThat(util.simplifyPath("/a/b/c/d")).isEqualTo("/a/b/c/d");
        assertThat(util.simplifyPath("/a/../b/")).isEqualTo("/b");
        assertThat(util.simplifyPath("/a/./b/c/../d")).isEqualTo("/a/b/d");
        assertThat(util.simplifyPath("./a")).isEqualTo("/a");
        assertThat(util.simplifyPath(null)).isEqualTo("");
        assertThat(util.simplifyPath("")).isEqualTo("");
    }

    @Test
    public void createParentDirTest() throws IOException {
        File child = mock(File.class);
        File parent = mock(File.class);
        when(child.getCanonicalFile()).thenReturn(child);
        when(child.getParentFile()).thenReturn(parent);
        when(parent.isDirectory()).thenReturn(true);
        util.createParentDirs(child);
        verify(parent).mkdirs();
    }

    static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }
}
