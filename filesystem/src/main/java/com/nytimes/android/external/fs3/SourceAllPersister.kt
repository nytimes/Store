package com.nytimes.android.external.fs3


import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.AllPersister
import com.nytimes.android.external.store3.base.impl.BarCode

import java.io.FileNotFoundException
import javax.inject.Inject

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import okio.BufferedSource

class SourceAllPersister @Inject
constructor(fileSystem: FileSystem) : AllPersister<BufferedSource, BarCode> {

    internal val sourceFileAllReader: FSAllReader
    internal val sourceFileAllEraser: FSAllEraser

    internal val sourceFileReader: FSReader<BarCode>
    internal val sourceFileWriter: FSWriter<BarCode>

    init {
        sourceFileAllReader = FSAllReader(fileSystem)
        sourceFileAllEraser = FSAllEraser(fileSystem)
        sourceFileReader = FSReader(fileSystem, BarCodeReadAllPathResolver())
        sourceFileWriter = FSWriter(fileSystem, BarCodeReadAllPathResolver())
    }

    @Throws(FileNotFoundException::class)
    override fun readAll(path: String): Observable<BufferedSource> {
        return sourceFileAllReader.readAll(path)
    }

    override fun deleteAll(path: String): Observable<Boolean> {
        return sourceFileAllEraser.deleteAll(path)
    }

    override suspend fun read(barCode: BarCode): BufferedSource? {
        return sourceFileReader.read(barCode)
    }

    override suspend fun write(barCode: BarCode, data: BufferedSource): Boolean {
        return sourceFileWriter.write(barCode, data)
    }

    companion object {

        fun create(fileSystem: FileSystem): SourceAllPersister {
            return SourceAllPersister(fileSystem)
        }
    }
}
