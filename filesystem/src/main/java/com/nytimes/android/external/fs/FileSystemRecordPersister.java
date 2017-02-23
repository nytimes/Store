package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.RecordProvider;
import com.nytimes.android.external.store.base.RecordState;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okio.BufferedSource;
import rx.Observable;

/**
 * FileSystemRecordPersister is used when persisting to/from file system while being stale aware
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 *
 * @param <Key> key type
 */
public final class FileSystemRecordPersister<Key> implements Persister<BufferedSource, Key>, RecordProvider<Key> {
    private final FSReader<Key> fileReader;
    private final FSWriter<Key> fileWriter;
    private final FileSystem fileSystem;
    private final PathResolver<Key> pathResolver;
    private final long expirationDuration;
    @Nonnull
    private final TimeUnit expirationUnit;

    private FileSystemRecordPersister(FileSystem fileSystem, PathResolver<Key> pathResolver,
                                      long expirationDuration,
                                      @Nonnull TimeUnit expirationUnit) {
        this.fileSystem = fileSystem;
        this.pathResolver = pathResolver;
        this.expirationDuration = expirationDuration;
        this.expirationUnit = expirationUnit;
        fileReader = new FSReader<>(fileSystem, pathResolver);
        fileWriter = new FSWriter<>(fileSystem, pathResolver);
    }

    @Nonnull
    public static <T> FileSystemRecordPersister<T> create(FileSystem fileSystem,
                                                          PathResolver<T> pathResolver,
                                                          long expirationDuration,
                                                          @Nonnull TimeUnit expirationUnit) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return new FileSystemRecordPersister<>(fileSystem, pathResolver,
                expirationDuration, expirationUnit);
    }

    @Nonnull
    @Override
    public RecordState getRecordState(@Nonnull Key key) {
        return fileSystem.getRecordState(expirationUnit, expirationDuration, pathResolver.resolve(key));
    }

    @Nonnull
    @Override
    public Observable<BufferedSource> read(@Nonnull Key key) {
        return fileReader.read(key);
    }

    @Nonnull
    @Override
    public Observable<Boolean> write(@Nonnull Key key, @Nonnull BufferedSource bufferedSource) {
        return fileWriter.write(key, bufferedSource);
    }
}
