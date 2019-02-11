package com.nytimes.android.external.fs3

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.File
import java.io.IOException

class UtilTest {

    private val util = Util()

    @Test
    fun testSimplifyPath() {
        assertThat(util.simplifyPath("/a/b/c/d")).isEqualTo("/a/b/c/d")
        assertThat(util.simplifyPath("/a/../b/")).isEqualTo("/b")
        assertThat(util.simplifyPath("/a/./b/c/../d")).isEqualTo("/a/b/d")
        assertThat(util.simplifyPath("./a")).isEqualTo("/a")
        assertThat(util.simplifyPath("")).isEqualTo("")
    }

    @Test
    @Throws(IOException::class)
    fun createParentDirTest() {
        val child = mock(File::class.java)
        val parent = mock(File::class.java)
        `when`(child.canonicalFile).thenReturn(child)
        `when`(child.parentFile).thenReturn(parent)
        `when`(parent.isDirectory).thenReturn(true)
        util.createParentDirs(child)
        verify(parent).mkdirs()
    }
}
