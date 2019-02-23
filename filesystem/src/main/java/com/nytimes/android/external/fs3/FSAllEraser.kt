package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.DiskAllErase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FSAllEraser(internal val fileSystem: FileSystem) : DiskAllErase {
    override suspend fun deleteAll(path: String): Boolean {
        return withContext(Dispatchers.IO) {
            fileSystem.deleteAll(path)
            true
        }
    }
}
