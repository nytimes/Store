package com.nytimes.android.external.store.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store.base.Parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import javax.inject.Inject;

import okio.BufferedSource;

public class JacksonSourceParser<Parsed> implements Parser<BufferedSource, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    public JacksonSourceParser(@NotNull JsonFactory jsonFactory, @NotNull Type type) {
        objectMapper = new ObjectMapper(jsonFactory);
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonSourceParser(@NotNull ObjectMapper objectMapper, @NotNull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    @Nullable
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public Parsed call(@NotNull BufferedSource source) {
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
