package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.DiskAllErase
import io.reactivex.Observable


class FSAllEraser(internal val fileSystem: FileSystem) : DiskAllErase {
    override fun deleteAll(path: String): Observable<Boolean> {
        return Observable.fromCallable {
            fileSystem.deleteAll(path)
            true
        }
    }
}
