package com.nytimes.android.external.store3.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.fs3.BufferedSourceAdapter;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import okio.Okio;

/**
 * An implementation of {@link BufferedSourceAdapter BufferedSourceAdapter} that uses
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
        return Okio.buffer(Okio.source(new ByteArrayInputStream(gson.toJson(value).getBytes(
                Charset.forName("UTF-8")))));
    }
}
