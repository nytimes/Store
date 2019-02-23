package com.nytimes.android.external.fs3


import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import okio.BufferedSource
import javax.inject.Inject

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

    protected val sourceFileReader: SourceFileReader = SourceFileReader(fileSystem)
    protected val sourceFileWriter: SourceFileWriter = SourceFileWriter(fileSystem)

    override suspend fun read(barCode: BarCode): BufferedSource? {
        return sourceFileReader.read(barCode)
    }

    override suspend fun write(barCode: BarCode, data: BufferedSource): Boolean {
        return sourceFileWriter.write(barCode, data)
    }

    companion object {

        fun create(fileSystem: FileSystem): SourcePersister {
            return SourcePersister(fileSystem)
        }

        internal fun pathForBarcode(barCode: BarCode): String {
            return barCode.type + barCode.key
        }
    }

}
