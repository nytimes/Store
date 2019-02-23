package com.nytimes.android.external.fs3


import com.google.common.base.Charsets.UTF_8
import com.google.common.io.Files.createTempDir
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.ByteArrayInputStream

class FSAllOperationTest {

    @Test
    fun readAll() = runBlocking<Unit> {
        val tempDir = createTempDir()
        val fileSystem = FileSystemFactory.create(tempDir)

        //write different data to File System for each barcode
        fileSystem.write("$FOLDER/key.txt", source(CHALLAH))
        fileSystem.write("$FOLDER/$INNER_FOLDER/key2.txt", source(CHALLAH_CHALLAH))
        val reader = FSAllReader(fileSystem)
        //read back all values for the FOLDER
        val observable = with(reader) { readAll(FOLDER) }
        assertThat(observable.receive().readUtf8()).isEqualTo(CHALLAH)
        assertThat(observable.receive().readUtf8()).isEqualTo(CHALLAH_CHALLAH)
    }

    @Test
    fun deleteAll() = runBlocking<Unit> {
        val tempDir = createTempDir()
        val fileSystem = FileSystemFactory.create(tempDir)
        //write different data to File System for each barcode
        fileSystem.write("$FOLDER/key.txt", source(CHALLAH))
        fileSystem.write("$FOLDER/$INNER_FOLDER/key2.txt", source(CHALLAH_CHALLAH))

        val eraser = FSAllEraser(fileSystem)
        assertThat(eraser.deleteAll(FOLDER)).isEqualTo(true)
    }

    companion object {

        val FOLDER = "type"
        val INNER_FOLDER = "type2"
        val CHALLAH = "Challah"
        val CHALLAH_CHALLAH = "Challah_CHALLAH"


        private fun source(data: String): BufferedSource {
            return Okio.buffer(Okio.source(ByteArrayInputStream(data.toByteArray(UTF_8))))
        }
    }

}
