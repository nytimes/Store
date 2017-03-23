package com.nytimes.android.external.fs2;

import com.nytimes.android.external.fs2.filesystem.FileSystem;
import com.nytimes.android.external.store.base.Persister;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import okio.BufferedSource;

/**
 * FileSystemPersister is used when persisting to/from file system
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 * @param <T> key type
 */
public final class FileSystemPersister<T> implements Persister<BufferedSource, T> {
    private final FSReader<T> fileReader;
    private final FSWriter<T> fileWriter;

    private FileSystemPersister(FileSystem fileSystem, PathResolver<T> pathResolver) {
        fileReader = new FSReader<>(fileSystem, pathResolver);
        fileWriter = new FSWriter<>(fileSystem, pathResolver);
    }

    @Nonnull
    public static <T> Persister<BufferedSource, T> create(FileSystem fileSystem,
                                                          PathResolver<T> pathResolver) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return new FileSystemPersister<>(fileSystem, pathResolver);
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> read(@Nonnull final T key) {
        return fileReader.read(key);
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(@Nonnull final T key, @Nonnull final BufferedSource data) {
        return fileWriter.write(key, data);
    }
}
