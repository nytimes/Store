package com.nytimes.android.external.fs3.filesystem;

import com.nytimes.android.external.store3.base.RecordState;
import okio.BufferedSource;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * This {@link FileSystem} has been created for testing purposes only, especially on Android, when you might need
 * to avoid storing all the information.
 *
 * It is not suitable for production.
 */
@Deprecated
public class InMemoryFileSystem implements FileSystem {

    private Map<String, BufferedSource> fs = new HashMap<>();

    @Nonnull
    @Override
    public BufferedSource read(String path) throws FileNotFoundException {
        if (fs.containsKey(path)) {
            return fs.get(path);
        } else {
            throw new FileNotFoundException("File " + path + " was not found in memory");
        }
    }

    @Override
    public void write(String path, BufferedSource source) throws IOException {
        fs.put(path, source);
    }

    @Override
    public void delete(String path) throws IOException {
        fs.remove(path);
    }

    @Override
    public void deleteAll(String path) throws IOException {
        fs.clear();
    }

    @Nonnull
    @Override
    public Collection<String> list(String path) throws FileNotFoundException {
        if (fs.isEmpty()) {
            throw new FileNotFoundException("Memory is empty");
        }
        return fs.keySet();
    }

    @Override
    public boolean exists(String file) {
        return fs.containsKey(file);
    }

    @Override
    public RecordState getRecordState(@Nonnull TimeUnit expirationUnit, long expirationDuration, @Nonnull String path) {
        if (fs.containsKey(path)) {
            return RecordState.FRESH;
        } else {
            return RecordState.MISSING;
        }
    }
}
