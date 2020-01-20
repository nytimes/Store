package com.nytimes.android.external.fs3;


import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.AllPersister;
import com.nytimes.android.external.store3.base.ReadResult;
import com.nytimes.android.external.store3.base.impl.BarCode;

import java.io.FileNotFoundException;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import okio.BufferedSource;

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
    public Observable<ReadResult<BufferedSource>> safeReadAll(@Nonnull String path) {
        return sourceFileAllReader.safeReadAll(path);
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
    public Maybe<BufferedSource> read(@Nonnull BarCode barCode) {
        return sourceFileReader.read(barCode);
    }

    @Nonnull
    @Override
    public Single<Boolean> write(@Nonnull BarCode barCode, @Nonnull BufferedSource data) {
        return sourceFileWriter.write(barCode, data);
    }

    public static SourceAllPersister create(FileSystem fileSystem) {
        return new SourceAllPersister(fileSystem);
    }
}
