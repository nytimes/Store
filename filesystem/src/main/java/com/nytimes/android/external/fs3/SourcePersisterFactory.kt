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
 * Factory for [SourcePersister]
 */

object SourcePersisterFactory {


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
        return RecordPersister.create(FileSystemFactory.create(root), expirationDuration, expirationUnit)
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
        return RecordPersister.create(fileSystem, expirationDuration, expirationUnit)
    }

    /**
     * Returns a new [BufferedSource] persister with the provided file as the root of the
     * persistence [FileSystem].
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun create(root: File): Persister<BufferedSource, BarCode> {
        if (root == null) {
            throw IllegalArgumentException("root file cannot be null.")
        }
        return SourcePersister.create(FileSystemFactory.create(root))
    }

    /**
     * Returns a new [BufferedSource] persister with the provided fileSystem as the root of the
     * persistence [FileSystem].
     */
    fun create(fileSystem: FileSystem): Persister<BufferedSource, BarCode> {
        if (fileSystem == null) {
            throw IllegalArgumentException("fileSystem cannot be null.")
        }
        return SourcePersister.create(fileSystem)
    }

    /**
     * Returns a new [BufferedSource] persister with the provided file as the root of the
     * persistence [FileSystem].
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createAll(root: File): Persister<BufferedSource, BarCode> {
        if (root == null) {
            throw IllegalArgumentException("root file cannot be null.")
        }
        return SourceAllPersister.create(FileSystemFactory.create(root))
    }

    /**
     * Returns a new [BufferedSource] persister with the provided fileSystem as the root of the
     * persistence [FileSystem].
     */
    fun createAll(fileSystem: FileSystem): Persister<BufferedSource, BarCode> {
        if (fileSystem == null) {
            throw IllegalArgumentException("fileSystem cannot be null.")
        }
        return SourceAllPersister.create(fileSystem)
    }
}
