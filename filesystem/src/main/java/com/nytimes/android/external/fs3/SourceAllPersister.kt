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

    private val sourceFileAllReader: FSAllReader by lazy { FSAllReader(fileSystem) }
    private val sourceFileAllEraser: FSAllEraser by lazy { FSAllEraser(fileSystem) }

    private val sourceFileReader: FSReader<BarCode> by lazy { FSReader(fileSystem, BarCodeReadAllPathResolver()) }
    private val sourceFileWriter: FSWriter<BarCode> by lazy { FSWriter(fileSystem, BarCodeReadAllPathResolver()) }

    @Throws(FileNotFoundException::class)
    override fun readAll(path: String): Observable<BufferedSource> = sourceFileAllReader.readAll(path)

    override fun deleteAll(path: String): Observable<Boolean> = sourceFileAllEraser.deleteAll(path)

    override fun read(barCode: BarCode): Maybe<BufferedSource> = sourceFileReader.read(barCode)

    override fun write(barCode: BarCode, data: BufferedSource): Single<Boolean> = sourceFileWriter.write(barCode, data)

    companion object {

        fun create(fileSystem: FileSystem): SourceAllPersister {
            return SourceAllPersister(fileSystem)
        }
    }
}
