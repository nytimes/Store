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
 * Factory for {@link RecordPersister}
 */

public final class RecordPersisterFactory {
    private RecordPersisterFactory() {
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
        return new RecordPersister(FileSystemFactory.create(root), expirationDuration, expirationUnit);
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
        return new RecordPersister(fileSystem, expirationDuration, expirationUnit);
    }

}
