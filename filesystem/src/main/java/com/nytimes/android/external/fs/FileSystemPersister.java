package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.Persister;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;

public class FileSystemPersister<T> implements Persister<BufferedSource, T> {
    final FileSystem fileSystem;
    final String filenamePrefix;
    private final FSReader<T> fileReader;
    private final FSWriter<Object> fileWriter;

    public FileSystemPersister(FileSystem fileSystem, String filenamePrefix) {

        fileReader = new FSReader<>(fileSystem, filenamePrefix);
        fileWriter = new FSWriter<>(fileSystem, filenamePrefix);
        this.fileSystem = fileSystem;
        this.filenamePrefix = filenamePrefix;
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> read(final T id) {
        return fileReader.read(id);
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(final T barCode, final BufferedSource data) {
        return fileWriter.write(barCode, data);
    }
}
