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
    final FSAllReader sourceFileAllReader;
    @Nonnull
    final FSAllEraser sourceFileAllEraser;

    @Nonnull
    final FSReader<BarCode> sourceFileReader;
    @Nonnull
    final FSWriter<BarCode> sourceFileWriter;

    @Inject
    public SourceAllPersister(FileSystem fileSystem) {
        sourceFileAllReader = new FSAllReader(fileSystem);
        sourceFileAllEraser = new FSAllEraser(fileSystem);
        sourceFileReader = new FSReader<>(fileSystem,  new BarCodeReadAllPathResolver());
        sourceFileWriter = new FSWriter<>(fileSystem,  new BarCodeReadAllPathResolver());
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

    @Nonnull
    @Override
    public Observable<BufferedSource> read(@Nonnull BarCode barCode) {
        return sourceFileReader.read(barCode);
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(@Nonnull BarCode barCode, @Nonnull BufferedSource data) {
        return sourceFileWriter.write(barCode, data);
    }
}
