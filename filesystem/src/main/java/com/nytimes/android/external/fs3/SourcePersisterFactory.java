package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory;
import com.nytimes.android.external.store3.base.Persister;
import com.nytimes.android.external.store3.base.impl.BarCode;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okio.BufferedSource;

/**
 * Factory for {@link SourcePersister}
 */

public final class SourcePersisterFactory {
    private SourcePersisterFactory() {
    }


    /**
     * Returns a new {@link BufferedSource} persister with the provided file as the root of the
     * persistence {@link FileSystem}.
     *
     * @throws IOException
     */
    @Nonnull
    public static Persister<BufferedSource, BarCode> create(@Nonnull File root,
                                                            long expirationDuration,
                                                            @Nonnull TimeUnit expirationUnit) throws IOException {
        if (root == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return RecordPersister.create(FileSystemFactory.create(root), expirationDuration, expirationUnit);
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided fileSystem as the root of the
     * persistence {@link FileSystem}.
     **/
    @Nonnull
    public static Persister<BufferedSource, BarCode> create(@Nonnull FileSystem fileSystem,
                                                            long expirationDuration,
                                                            @Nonnull TimeUnit expirationUnit) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("fileSystem cannot be null.");
        }
        return RecordPersister.create(fileSystem, expirationDuration, expirationUnit);
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided file as the root of the
     * persistence {@link FileSystem}.
     *
     * @throws IOException
     */
    @Nonnull
    public static Persister<BufferedSource, BarCode> create(@Nonnull File root) throws IOException {
        if (root == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return SourcePersister.create(FileSystemFactory.create(root));
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided fileSystem as the root of the
     * persistence {@link FileSystem}.
     **/
    @Nonnull
    public static Persister<BufferedSource, BarCode> create(@Nonnull FileSystem fileSystem) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("fileSystem cannot be null.");
        }
        return SourcePersister.create(fileSystem);
    }


    /**
     * Returns a new {@link BufferedSource} persister with the provided file as the root of the
     * persistence {@link FileSystem}.
     *
     * @throws IOException
     */
    @Nonnull
    public static Persister<BufferedSource, BarCode> createAll(@Nonnull File root) throws IOException {
        if (root == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return SourceAllPersister.create(FileSystemFactory.create(root));
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided fileSystem as the root of the
     * persistence {@link FileSystem}.
     **/
    @Nonnull
    public static Persister<BufferedSource, BarCode> createAll(@Nonnull FileSystem fileSystem) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("fileSystem cannot be null.");
        }
        return SourceAllPersister.create(fileSystem);
    }

}
