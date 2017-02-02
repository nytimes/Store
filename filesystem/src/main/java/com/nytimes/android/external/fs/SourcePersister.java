package com.nytimes.android.external.fs;


import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

/**
 * Persister to be used when storing something to persister from a BufferedSource
 * example usage:
 * ParsingStoreBuilder.<BufferedSource, BookResults>builder()
 * .fetcher(fetcher)
 * .persister(new SourcePersister(fileSystem))
 * .parser(new GsonSourceParser<>(gson, BookResults.class))
 * .open();
 */
public class SourcePersister implements Persister<BufferedSource> {

    @Nonnull
    final SourceFileReader sourceFileReader;
    @Nonnull
    final SourceFileWriter sourceFileWriter;

    @Inject
    public SourcePersister(FileSystem fileSystem) {
        sourceFileReader = new SourceFileReader(fileSystem);
        sourceFileWriter = new SourceFileWriter(fileSystem);
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> read(@Nonnull final BarCode barCode) {
        return sourceFileReader.exists(barCode) ? sourceFileReader.read(barCode) : Observable.<BufferedSource>empty();
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(@Nonnull final BarCode barCode, @Nonnull final BufferedSource data) {
        return sourceFileWriter.write(barCode, data);
    }


    @Nonnull
    static String pathForBarcode(@Nonnull BarCode barCode) {
        return barCode.getType() + barCode.getKey();
    }

}
