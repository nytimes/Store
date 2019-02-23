package com.nytimes.android.external.fs3


import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.DiskErase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.BufferedSource

class FSEraser<T>(
        internal val fileSystem: FileSystem,
        internal val pathResolver: PathResolver<T>
) : DiskErase<BufferedSource, T> {

    override suspend fun delete(key: T): Boolean {
        return withContext(Dispatchers.IO) {
            fileSystem.delete(pathResolver.resolve(key))
            true
        }
    }
}
