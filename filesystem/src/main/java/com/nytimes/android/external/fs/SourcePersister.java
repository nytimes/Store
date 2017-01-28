package com.nytimes.android.external.fs;


import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    private final SourceFileReader sourceFileReader;
    @NotNull
    private final SourceFileWriter sourceFileWriter;

    @Inject
    public SourcePersister(FileSystem fileSystem) {
        sourceFileReader = new SourceFileReader(fileSystem);
        sourceFileWriter = new SourceFileWriter(fileSystem);
    }

    @NotNull
    @Override
    public Observable<BufferedSource> read(@NotNull final BarCode barCode) {
        return sourceFileReader.exists(barCode) ? sourceFileReader.read(barCode) : Observable.<BufferedSource>empty();
    }

    @NotNull
    @Override
    public Observable<Boolean> write(@NotNull final BarCode barCode, @NotNull final BufferedSource data) {
        return sourceFileWriter.write(barCode, data);
    }


    @NotNull
    static String pathForBarcode(@NotNull BarCode barCode) {
        return barCode.getType() + barCode.getKey();
    }

}
