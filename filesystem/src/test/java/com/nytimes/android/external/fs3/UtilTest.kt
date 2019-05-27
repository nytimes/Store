package com.nytimes.android.external.fs3

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.File
import java.io.IOException

class UtilTest {

    @Test
    fun testSimplifyPath() {
        assertThat(Util.simplifyPath("/a/b/c/d")).isEqualTo("/a/b/c/d")
        assertThat(Util.simplifyPath("/a/../b/")).isEqualTo("/b")
        assertThat(Util.simplifyPath("/a/./b/c/../d")).isEqualTo("/a/b/d")
        assertThat(Util.simplifyPath("./a")).isEqualTo("/a")
        assertThat(Util.simplifyPath("")).isEqualTo("")
    }

    @Test
    @Throws(IOException::class)
    fun createParentDirTest() {
        val child = mock(File::class.java)
        val parent = mock(File::class.java)
        whenever(child.canonicalFile) doReturn child
        whenever(child.parentFile) doReturn parent
        whenever(parent.isDirectory) doReturn true
        Util.createParentDirs(child)
        verify(parent).mkdirs()
    }
}
