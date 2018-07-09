package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

import okio.BufferedSource

/**
 * Factory for [RecordPersister]
 */

object RecordPersisterFactory {

    /**
     * Returns a new [BufferedSource] persister with the provided file as the root of the
     * persistence [FileSystem].
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun create(root: File,
               expirationDuration: Long,
               expirationUnit: TimeUnit): Persister<BufferedSource, BarCode> {
        if (root == null) {
            throw IllegalArgumentException("root file cannot be null.")
        }
        return RecordPersister(FileSystemFactory.create(root), expirationDuration, expirationUnit)
    }

    /**
     * Returns a new [BufferedSource] persister with the provided fileSystem as the root of the
     * persistence [FileSystem].
     */
    fun create(fileSystem: FileSystem,
               expirationDuration: Long,
               expirationUnit: TimeUnit): Persister<BufferedSource, BarCode> {
        if (fileSystem == null) {
            throw IllegalArgumentException("fileSystem cannot be null.")
        }
        return RecordPersister(fileSystem, expirationDuration, expirationUnit)
    }

}
