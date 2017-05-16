package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.fs.BufferedSourceAdapter;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import okio.Okio;

/**
 * An implementation of {@link com.nytimes.android.external.fs.BufferedSourceAdapter BufferedSourceAdapter} that uses
 * {@link com.google.gson.Gson Gson} to convert Java values to JSON.
 */
public class GsonBufferedSourceAdapter<Parsed> implements BufferedSourceAdapter<Parsed> {

    private final Gson gson;

    @Inject
    public GsonBufferedSourceAdapter(@Nonnull Gson gson) {
        this.gson = gson;
    }

    @Nonnull
    @Override
    public BufferedSource toJson(@Nonnull Parsed value) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(gson.toJson(value).getBytes(StandardCharsets.UTF_8))));
    }
}
