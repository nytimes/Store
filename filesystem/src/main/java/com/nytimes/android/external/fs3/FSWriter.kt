package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.DiskWrite
import okio.BufferedSource

/**
 * FSReader is used when persisting to file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 * @param <T> key type
</T> */
open class FSWriter<T>(internal val fileSystem: FileSystem, internal val pathResolver: PathResolver<T>) :
        DiskWrite<BufferedSource, T> {

    override suspend fun write(key: T, data: BufferedSource): Boolean {
        fileSystem.write(pathResolver.resolve(key), data)
        return true

    }
}
