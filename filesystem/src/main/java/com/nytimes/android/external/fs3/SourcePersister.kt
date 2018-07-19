package com.nytimes.android.external.fs3


import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import javax.inject.Inject

import io.reactivex.Maybe
import io.reactivex.Single
import okio.BufferedSource

/**
 * Persister to be used when storing something to persister from a BufferedSource
 * example usage:
 * ParsingStoreBuilder.<BufferedSource></BufferedSource>, BookResults>builder()
 * .fetcher(fetcher)
 * .persister(new SourcePersister(fileSystem))
 * .parser(new GsonSourceParser<>(gson, BookResults.class))
 * .open();
 */
open class SourcePersister @Inject
constructor(fileSystem: FileSystem) : Persister<BufferedSource, BarCode> {

    internal val sourceFileReader: SourceFileReader by lazy { SourceFileReader(fileSystem) }
    internal val sourceFileWriter: SourceFileWriter by lazy { SourceFileWriter(fileSystem)}

    override fun read(barCode: BarCode): Maybe<BufferedSource> = sourceFileReader.read(barCode)

    override fun write(barCode: BarCode, data: BufferedSource): Single<Boolean> = sourceFileWriter.write(barCode, data)

    companion object {

        fun create(fileSystem: FileSystem): SourcePersister = SourcePersister(fileSystem)

        internal fun pathForBarcode(barCode: BarCode): String = barCode.type + barCode.key
    }
}
