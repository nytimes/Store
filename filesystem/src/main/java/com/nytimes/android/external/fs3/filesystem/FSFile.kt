package com.nytimes.android.external.fs3.filesystem


import com.nytimes.android.external.fs3.Util
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.String.format

internal class FSFile(root: File, private val pathValue: String) {
    private val file = File(root, pathValue)

    init {
        if (file.exists() && file.isDirectory) {
            throw FileNotFoundException(format("expecting a file at %s, instead found a directory", pathValue))
        }
        Util.createParentDirs(this.file)
    }

    fun exists(): Boolean = file.exists()

    fun delete() {
        /**
         * it's ok to delete the file even if we still have readers! the file won't really
         * be deleted until all readers close it (it just removes the name-to-inode mapping)
         */
        if (!file.delete()) {
            throw IllegalStateException("unable to delete $file")
        }
    }

    fun path(): String = pathValue

    @Throws(IOException::class)
    fun write(source: BufferedSource) {
        val tmpFile = File.createTempFile("new", "tmp", file.parentFile)
        var sink: BufferedSink? = null
        try {

            sink = Okio.buffer(Okio.sink(tmpFile))
            sink!!.writeAll(source)

            if (!tmpFile.renameTo(file)) {
                throw IOException("unable to move tmp file to " + file.path)
            }
        } catch (e: Exception) {
            throw IOException("unable to write to file", e)

        } finally {
            tmpFile.delete()
            sink?.close()
            source.close()
        }
    }


    @Throws(FileNotFoundException::class)
    fun source(): BufferedSource {
        if (file.exists()) {
            return Okio.buffer(Okio.source(file))
        }
        throw FileNotFoundException(pathValue)
    }

    fun lastModified(): Long = file.lastModified()
}

