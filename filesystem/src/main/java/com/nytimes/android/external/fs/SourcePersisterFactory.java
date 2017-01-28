package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.fs.filesystem.FileSystemFactory;
import com.nytimes.android.external.store.base.Persister;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

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
    @NotNull
    public static Persister<BufferedSource> create(@NotNull File root) throws IOException {
        if (root == null) {
            throw new IllegalArgumentException("root file cannot be null.");
        }
        return new SourcePersister(FileSystemFactory.create(root));
    }

    /**
     * Returns a new {@link BufferedSource} persister with the provided fileSystem as the root of the
     * persistence {@link com.nytimes.android.external.fs.filesystem.FileSystem}.
     *
     * @throws IOException
     */
    @NotNull
    public static Persister<BufferedSource> create(@NotNull FileSystem fileSystem) throws IOException {
        if (fileSystem == null) {
            throw new IllegalArgumentException("fileSystem cannot be null.");
        }
        return new SourcePersister(fileSystem);
    }
}
