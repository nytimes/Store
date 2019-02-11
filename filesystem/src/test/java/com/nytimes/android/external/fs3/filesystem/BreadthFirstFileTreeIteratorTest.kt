package com.nytimes.android.external.fs3.filesystem

import org.junit.Before
import org.junit.Test

import java.io.File
import java.io.IOException

import org.assertj.core.api.Assertions.assertThat

class BreadthFirstFileTreeIteratorTest {

    private lateinit var systemTempDir: File

    @Before
    @Throws(IOException::class)
    fun setUp() {
        val property = "java.io.tmpdir"
        systemTempDir = createDirWithSubFiles(File(System.getProperty(property)), 0)
    }

    @Test
    @Throws(IOException::class)
    fun testHasNextEmpty() {
        val hasNextDir = createDirWithSubFiles(systemTempDir, 0)
        val btfti = BreadthFirstFileTreeIterator(hasNextDir)
        assertThat(btfti.hasNext()).isFalse()
    }

    @Test
    @Throws(IOException::class)
    fun testHasNextOne() {
        val hasNextDir = createDirWithSubFiles(systemTempDir, 1)
        val btfti = BreadthFirstFileTreeIterator(hasNextDir)
        assertThat(btfti.hasNext()).isTrue()
        assertThat(btfti.next()).isNotNull()
        assertThat(btfti.hasNext()).isFalse()
    }

    @Test
    @Throws(IOException::class)
    fun testHastNextMany() {
        val fileCount = 30
        val hasNextDir = createDirWithSubFiles(systemTempDir, fileCount)
        createDirWithSubFiles(hasNextDir, fileCount)
        val btfti = BreadthFirstFileTreeIterator(hasNextDir)
        var counter = 0
        while (btfti.hasNext()) {
            btfti.next()
            counter++
        }
        assertThat(counter).isEqualTo(fileCount * 2)
    }

    @Throws(IOException::class)
    private fun createDirWithSubFiles(root: File, fileCount: Int): File {
        assertThat(root).exists()
        assertThat(root.isDirectory).isTrue()

        val tempDir = createDir(root)
        for (i in 0 until fileCount) {
            createFile(tempDir)
        }
        return tempDir
    }

    @Throws(IOException::class)
    private fun createFile(root: File) {
        val someFile = File(root, "somefile" + System.nanoTime())
        assertThat(someFile.createNewFile()).isTrue()
        someFile.deleteOnExit()
    }

    private fun createDir(root: File): File {
        val label = "BFTI_test_" + System.nanoTime()
        val tempDir = File(root, label)

        assertThat(tempDir.mkdir()).isTrue()
        assertThat(tempDir.exists()).isTrue()
        assertThat(tempDir.isDirectory).isTrue()
        tempDir.deleteOnExit()
        return tempDir
    }
}
