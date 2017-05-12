package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.fs.BufferedSourceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import okio.Okio;

/**
 * An implementation of {@link com.nytimes.android.external.fs.BufferedSourceAdapter} that uses
 * {@link com.google.gson.Gson} to convert Java values to JSON, and JSON values to Java.
 */
public class TwoWayParserAdapter<Parsed> implements BufferedSourceAdapter<Parsed> {

    private final Gson gson;
    private final Type type;

    @Inject
    public TwoWayParserAdapter(@Nonnull Gson gson, @Nonnull Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Nonnull
    @Override
    public BufferedSource toJson(@Nonnull Parsed value) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(gson.toJson(value).getBytes(StandardCharsets.UTF_8))));
    }

    @Nonnull
    @Override
    public Parsed fromJson(@Nonnull BufferedSource source) {
        try (InputStreamReader reader = new InputStreamReader(source.inputStream(), Charset.forName("UTF-8"))) {
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
