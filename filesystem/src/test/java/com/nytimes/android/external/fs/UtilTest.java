package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.impl.BaseTestCase;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UtilTest extends BaseTestCase {

    private final Util util = new Util();

    @Test
    public void simplifyPathTest() {
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
}
