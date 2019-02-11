package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
import com.nytimes.android.external.store3.base.Persister

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

import okio.BufferedSource

object FileSystemPersisterFactory {

    /**
     * Returns a new [BufferedSource] persister with the provided file as the root of the
     * persistence [FileSystem].
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun <Key> create(root: File,
                     pathResolver: PathResolver<Key>): Persister<BufferedSource, Key> {
        if (root == null) {
            throw IllegalArgumentException("root file cannot be null.")
        }
        return FileSystemPersister.create(FileSystemFactory.create(root), pathResolver)
    }

    /**
     * Returns a new [BufferedSource] persister with the provided fileSystem as the root of the
     * persistence [FileSystem].
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun <Key> create(fileSystem: FileSystem,
                     pathResolver: PathResolver<Key>): Persister<BufferedSource, Key> {
        if (fileSystem == null) {
            throw IllegalArgumentException("root file cannot be null.")
        }
        return FileSystemPersister.create(fileSystem, pathResolver)
    }

    /**
     * Returns a new [BufferedSource] persister with the provided file as the root of the
     * persistence [FileSystem].
     *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun <Key> create(root: File,
                     pathResolver: PathResolver<Key>,
                     expirationDuration: Long,
                     expirationUnit: TimeUnit): Persister<BufferedSource, Key> {
        if (root == null) {
            throw IllegalArgumentException("root file cannot be null.")
        }
        return FileSystemRecordPersister.create(FileSystemFactory.create(root), pathResolver,
                expirationDuration, expirationUnit)
    }

    /**
     * Returns a new [BufferedSource] persister with the provided fileSystem as the root of the
     * persistence [FileSystem].
     */
    fun <Key> create(fileSystem: FileSystem,
                     pathResolver: PathResolver<Key>,
                     expirationDuration: Long,
                     expirationUnit: TimeUnit): Persister<BufferedSource, Key> {
        if (fileSystem == null) {
            throw IllegalArgumentException("fileSystem cannot be null.")
        }
        return FileSystemRecordPersister.create(fileSystem, pathResolver, expirationDuration,
                expirationUnit)
    }
}
