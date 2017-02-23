package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.fs.filesystem.FileSystemFactory;
import com.nytimes.android.external.store.base.Persister;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okio.BufferedSource;

public final class FileSystemPersisterFactory {

    private FileSystemPersisterFactory() {
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided file as the root of the
     * persistence {@link com.nytimes.android.external.fs.filesystem.FileSystem}.
     *
     * @throws IOException
     */
    @Nonnull
    public static <Key> Persister<BufferedSource, Key> create(@Nonnull File root,
                                                              PathResolver<Key> pathResolver) throws IOException {
        if (root == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return FileSystemPersister.create(FileSystemFactory.create(root), pathResolver);
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided fileSystem as the root of the
     * persistence {@link com.nytimes.android.external.fs.filesystem.FileSystem}.
     *
     * @throws IOException
     */
    @Nonnull
    public static <Key> Persister<BufferedSource, Key> create(@Nonnull FileSystem fileSystem,
                                                              PathResolver<Key> pathResolver) throws IOException {
        if (fileSystem == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return FileSystemPersister.create(fileSystem, pathResolver);
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided file as the root of the
     * persistence {@link com.nytimes.android.external.fs.filesystem.FileSystem}.
     *
     * @throws IOException
     */
    @Nonnull
    public static <Key> Persister<BufferedSource, Key> create(@Nonnull File root,
                                                              PathResolver<Key> pathResolver,
                                                              long expirationDuration,
                                                              @Nonnull TimeUnit expirationUnit)
            throws IOException {
        if (root == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return FileSystemRecordPersister.create(FileSystemFactory.create(root), pathResolver,
                expirationDuration, expirationUnit);
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided fileSystem as the root of the
     * persistence {@link com.nytimes.android.external.fs.filesystem.FileSystem}.
     **/
    @Nonnull
    public static <Key> Persister<BufferedSource, Key> create(@Nonnull FileSystem fileSystem,
                                                              PathResolver<Key> pathResolver,
                                                              long expirationDuration,
                                                              @Nonnull TimeUnit expirationUnit) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("fileSystem cannot be null.");
        }
        return FileSystemRecordPersister.create(fileSystem, pathResolver, expirationDuration,
                expirationUnit);
    }
}
