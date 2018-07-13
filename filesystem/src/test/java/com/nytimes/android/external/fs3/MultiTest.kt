package com.nytimes.android.external.fs3

import com.google.common.base.Charsets.UTF_8
import com.google.common.io.Files.createTempDir
import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
import okio.BufferedSource
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

class MultiTest {

    @Throws(IOException::class)
    private fun createAndPopulateTestFileSystem(): FileSystem {
        val baseDir = createTempDir()
        val fileSystem = FileSystemFactory.create(baseDir)
        for (path in fileData.keys) {
            for (data in fileData[path]!!) {
                val source = source(data)
                fileSystem.write(path, source)
                source.close()
            }
        }
        assertThat(fileSystem.list("/").size).isEqualTo(fileData.size)
        return fileSystem
    }

    @Test
    @Throws(IOException::class)
    fun testDeleteAll() {
        val fileSystem = createAndPopulateTestFileSystem()
        fileSystem.deleteAll("/")
        assertThat(fileSystem.list("/").size).isZero()
    }

    @Test
    @Throws(IOException::class)
    fun listNCompare() {
        val fileSystem = createAndPopulateTestFileSystem()
        var assertCount = 0
        for (path in fileSystem.list("/")) {
            val data = fileSystem.read(path).readUtf8()
            val written = fileData[path]
            val writtenData = written?.get(written.size - 1)
            assertThat(data).isEqualTo(writtenData)
            assertCount++
        }
        assertThat(assertCount).isEqualTo(fileData.size)
    }

    companion object {

        private val fileData: Map<String, List<String>> = mapOf("/foo/bar.txt" to listOf("sfvSFv", "AsfgasFgae", "szfvzsfbzdsfb"),
                "/foo/bar/baz.xyz" to listOf("sasffvSFv", "AsfgsdvzsfbvasFgae", "szfvzsfszfvzsvbzdsfb"))

        private fun source(data: String): BufferedSource = Okio.buffer(Okio.source(ByteArrayInputStream(data.toByteArray(UTF_8))))
    }
}
