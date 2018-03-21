package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.Clearable;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.RecordProvider;
import com.nytimes.android.external.store3.base.RecordState;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import okio.BufferedSource;

/**
 * FileSystemRecordPersister is used when persisting to/from file system while being stale aware
 * PathResolver will be used in creating file system paths based on cache keys.
 * Make sure to have keys containing same data resolve to same "path"
 *
 * @param <Key> key type
 */
public final class FileSystemRecordPersister<Key> implements Persister<BufferedSource, Key>,
        Clearable<Key>, RecordProvider<Key> {
    private final FSReader<Key> fileReader;
    private final FSWriter<Key> fileWriter;
    private final FSEraser<Key> fileEraser;
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
        fileEraser = new FSEraser<>(fileSystem, pathResolver);
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
    public Maybe<BufferedSource> read(@Nonnull Key key) {
        return fileReader.read(key);
    }

    @Nonnull
    @Override
    public Single<Boolean> write(@Nonnull Key key, @Nonnull BufferedSource bufferedSource) {
        return fileWriter.write(key, bufferedSource);
    }

    @Override
    public Completable clear(@Nonnull Key key) {
        return fileEraser.delete(key).toCompletable();
    }
}
