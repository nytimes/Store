package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.RecordProvider
import com.nytimes.android.external.store3.base.RecordState

import java.util.concurrent.TimeUnit

import io.reactivex.Maybe
import io.reactivex.Single
import okio.BufferedSource

/**
 * FileSystemRecordPersister is used when persisting to/from file system while being stale aware
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 *
 * @param <Key> key type
</Key> */
class FileSystemRecordPersister<Key> private constructor(private val fileSystem: FileSystem, private val pathResolver: PathResolver<Key>,
                                                         private val expirationDuration: Long,
                                                         private val expirationUnit: TimeUnit) : Persister<BufferedSource, Key>, RecordProvider<Key> {
    private val fileReader: FSReader<Key>
    private val fileWriter: FSWriter<Key>

    init {
        fileReader = FSReader(fileSystem, pathResolver)
        fileWriter = FSWriter(fileSystem, pathResolver)
    }

    override fun getRecordState(key: Key): RecordState {
        return fileSystem.getRecordState(expirationUnit, expirationDuration, pathResolver.resolve(key))
    }

    override suspend fun read(key: Key): BufferedSource? {
        return fileReader.read(key)
    }

    override suspend fun write(key: Key, bufferedSource: BufferedSource): Boolean {
        return fileWriter.write(key, bufferedSource)
    }

    companion object {

        fun <T> create(fileSystem: FileSystem?,
                       pathResolver: PathResolver<T>,
                       expirationDuration: Long,
                       expirationUnit: TimeUnit): FileSystemRecordPersister<T> {
            if (fileSystem == null) {
                throw IllegalArgumentException("root file cannot be null.")
            }
            return FileSystemRecordPersister(fileSystem, pathResolver,
                    expirationDuration, expirationUnit)
        }
    }
}