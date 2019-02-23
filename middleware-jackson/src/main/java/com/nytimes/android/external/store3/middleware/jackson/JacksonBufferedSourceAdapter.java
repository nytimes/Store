package com.nytimes.android.external.store3.middleware.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.fs3.BufferedSourceAdapter;

import java.io.ByteArrayInputStream;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import okio.BufferedSource;
import okio.Okio;

/**
 * An implementation of {@link BufferedSourceAdapter} that uses {@link ObjectMapper} to convert Java values to JSON.
 */
public class JacksonBufferedSourceAdapter<Parsed> implements BufferedSourceAdapter<Parsed> {

    private final ObjectMapper objectMapper;

    @Inject
    public JacksonBufferedSourceAdapter(@Nonnull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nonnull
    @Override
    public BufferedSource toJson(@Nonnull Parsed value) {
        try {
            return Okio.buffer(Okio.source(new ByteArrayInputStream(objectMapper.writeValueAsBytes(value))));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
