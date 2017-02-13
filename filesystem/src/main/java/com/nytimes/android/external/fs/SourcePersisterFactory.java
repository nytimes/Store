package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.fs.filesystem.FileSystemFactory;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;

import java.io.File;
import java.io.IOException;

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
     * persistence {@link com.nytimes.android.external.fs.filesystem.FileSystem}.
     *
     * @throws IOException
     */
    @Nonnull
    public static Persister<BufferedSource, BarCode> create(@Nonnull File root) throws IOException {
        if (root == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return new SourcePersister(FileSystemFactory.create(root));
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided fileSystem as the root of the
     * persistence {@link com.nytimes.android.external.fs.filesystem.FileSystem}.
     **/
    @Nonnull
    public static Persister<BufferedSource, BarCode> create(@Nonnull FileSystem fileSystem) {
        if (fileSystem == null) {
            throw new IllegalArgumentException("fileSystem cannot be null.");
        }
        return new SourcePersister(fileSystem);
    }

}
