package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.Persister

import io.reactivex.Maybe
import io.reactivex.Single
import okio.BufferedSource

/**
 * FileSystemPersister is used when persisting to/from file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 * @param <T> key type
</T> */
class FileSystemPersister<T> private constructor(fileSystem: FileSystem, pathResolver: PathResolver<T>) : Persister<BufferedSource, T> {
    private val fileReader: FSReader<T>
    private val fileWriter: FSWriter<T>

    init {
        fileReader = FSReader(fileSystem, pathResolver)
        fileWriter = FSWriter(fileSystem, pathResolver)
    }

    override suspend fun read(key: T): BufferedSource? {
        return fileReader.read(key)
    }

    override suspend fun write(key: T, data: BufferedSource): Boolean {
        return fileWriter.write(key, data)
    }

    companion object {

        fun <T> create(fileSystem: FileSystem?,
                       pathResolver: PathResolver<T>): Persister<BufferedSource, T> {
            if (fileSystem == null) {
                throw IllegalArgumentException("root file cannot be null.")
            }
            return FileSystemPersister(fileSystem, pathResolver)
        }
    }
}
