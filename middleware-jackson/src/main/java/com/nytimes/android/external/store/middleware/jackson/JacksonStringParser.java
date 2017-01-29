package com.nytimes.android.external.store.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store.base.Parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

public class JacksonStringParser<Parsed> implements Parser<String, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    public JacksonStringParser(@NotNull JsonFactory jsonFactory, @NotNull Type type) {
        objectMapper = new ObjectMapper(jsonFactory);
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonStringParser(@NotNull ObjectMapper objectMapper, @NotNull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    @Nullable
    public Parsed call(@NotNull String source) {
        try {
            return objectMapper.readValue(source, parsedType);
        } catch (IOException e) {
            return null;
        }
    }
}
