package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.fs.BufferedSourceAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import okio.Buffer;
import okio.BufferedSource;

/**
 * An implementation of {@link com.nytimes.android.external.fs.BufferedSourceAdapter BufferedSourceAdapter} that uses
 * {@link Moshi} to convert Java values to JSON.
 */
public class MoshiBufferedSourceAdapter<Parsed> implements BufferedSourceAdapter<Parsed> {

    private final JsonAdapter<Parsed> jsonAdapter;

    @Inject
    public MoshiBufferedSourceAdapter(@Nonnull Moshi moshi, @Nonnull Type type) {
        this.jsonAdapter = moshi.adapter(type);
    }

    @Nonnull
    @Override
    public BufferedSource toJson(@Nonnull Parsed value) {
        Buffer buffer = new Buffer();
        try {
            jsonAdapter.toJson(buffer, value);
        } catch (IOException e) {
            throw new AssertionError(e); // No I/O writing to a Buffer.
        }
        return buffer;
    }
}
