package com.nytimes.android.external.fs;

import android.support.annotation.NonNull;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.IBarCode;
import com.nytimes.android.external.store.base.Persister;

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

    @NonNull
    private final SourceFileReader sourceFileReader;
    @NonNull
    private final SourceFileWriter sourceFileWriter;

    @Inject
    public SourcePersister(FileSystem fileSystem) {
        this.sourceFileReader = new SourceFileReader(fileSystem);
        sourceFileWriter = new SourceFileWriter(fileSystem);
    }

    @NonNull
    @Override
    public Observable<BufferedSource> read(@NonNull final IBarCode IBarCode) {
        return sourceFileReader.exists(IBarCode) ? sourceFileReader.read(IBarCode) : Observable.<BufferedSource>empty();
    }

    @NonNull
    @Override
    public Observable<Boolean> write(@NonNull final IBarCode IBarCode, @NonNull final BufferedSource data) {
        return sourceFileWriter.write(IBarCode, data);
    }


    @NonNull
    static String pathForBarcode(@NonNull IBarCode IBarCode) {
        return IBarCode.getType() + IBarCode.getKey();
    }

}
