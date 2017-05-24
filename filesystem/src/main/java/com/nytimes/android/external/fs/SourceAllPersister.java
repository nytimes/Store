package com.nytimes.android.external.fs;


import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.AllPersister;


import java.io.FileNotFoundException;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

public class SourceAllPersister implements AllPersister<BufferedSource> {

    @Nonnull
    final FSAllReader sourceFileAllReader;
    @Nonnull
    final FSAllEraser sourceFileAllEraser;

    @Inject
    public SourceAllPersister(FileSystem fileSystem) {
        sourceFileAllReader = new FSAllReader(fileSystem);
        sourceFileAllEraser = new FSAllEraser(fileSystem);
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> readAll(@Nonnull final String path) throws FileNotFoundException {
        return sourceFileAllReader.readAll(path);
    }

    @Nonnull
    @Override
    public Observable<Boolean> deleteAll(@Nonnull final String path) {
        return sourceFileAllEraser.deleteAll(path);
    }
}
