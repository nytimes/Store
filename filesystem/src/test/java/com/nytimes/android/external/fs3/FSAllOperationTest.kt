package com.nytimes.android.external.fs3


import com.google.common.base.Charsets.UTF_8
import com.google.common.io.Files.createTempDir
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
import okio.BufferedSource
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

class FSAllOperationTest {

    @Test
    @Throws(IOException::class)
    fun readAll() {
        val tempDir = createTempDir()
        val fileSystem = FileSystemFactory.create(tempDir)

        //write different data to File System for each barcode
        fileSystem.write("$FOLDER/key.txt", source(CHALLAH))
        fileSystem.write("$FOLDER/$INNER_FOLDER/key2.txt", source(CHALLAH_CHALLAH))
        val reader = FSAllReader(fileSystem)
        //read back all values for the FOLDER
        val observable = reader.readAll(FOLDER)
        assertThat(observable.blockingFirst().readUtf8()).isEqualTo(CHALLAH)
        assertThat(observable.blockingLast().readUtf8()).isEqualTo(CHALLAH_CHALLAH)
    }

    @Test
    @Throws(IOException::class)
    fun deleteAll() {
        val tempDir = createTempDir()
        val fileSystem = FileSystemFactory.create(tempDir)
        //write different data to File System for each barcode
        fileSystem.write("$FOLDER/key.txt", source(CHALLAH))
        fileSystem.write("$FOLDER/$INNER_FOLDER/key2.txt", source(CHALLAH_CHALLAH))

        val eraser = FSAllEraser(fileSystem)
        val observable = eraser.deleteAll(FOLDER)
        assertThat(observable.blockingFirst()).isEqualTo(true)
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
