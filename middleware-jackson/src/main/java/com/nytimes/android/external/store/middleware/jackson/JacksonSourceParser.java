package com.nytimes.android.external.store.middleware.jackson;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store.base.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import javax.inject.Inject;

import okio.BufferedSource;

public class JacksonSourceParser<Parsed> implements Parser<BufferedSource, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    @Inject
    public JacksonSourceParser(@NonNull JsonFactory jsonFactory, @NonNull Type type) {
        objectMapper = new ObjectMapper(jsonFactory);
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonSourceParser(@NonNull ObjectMapper objectMapper, @NonNull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    @Nullable
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public Parsed call(@NonNull BufferedSource source) {
        InputStream inputStream = source.inputStream();
        try {
            return objectMapper.readValue(inputStream, parsedType);
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
