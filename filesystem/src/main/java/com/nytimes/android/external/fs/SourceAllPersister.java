package com.nytimes.android.external.fs;


import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.AllPersister;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.io.FileNotFoundException;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

public class SourceAllPersister implements AllPersister<BufferedSource, BarCode> {

    @Nonnull
    final SourceFileReader sourceFileReader;
    @Nonnull
    final SourceFileEraser sourceFileEraser;

    @Inject
    public SourceAllPersister(FileSystem fileSystem) {
        sourceFileReader = new SourceFileReader(fileSystem, new BarCodeReadAllPathResolver());
        sourceFileEraser = new SourceFileEraser(fileSystem, new BarCodeReadAllPathResolver());
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> readAll(@Nonnull final BarCode barCode) throws FileNotFoundException {
        return sourceFileReader.readAll(barCode);
    }

    @Nonnull
    @Override
    public Observable<Boolean> deleteAll(@Nonnull final BarCode barCode) {
        return sourceFileEraser.deleteAll(barCode);
    }
}
