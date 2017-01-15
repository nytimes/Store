package com.nytimes.android.external.store.middleware.jackson;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store.base.Parser;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.inject.Inject;

public class JacksonStringParser<Parsed> implements Parser<String, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    @Inject
    public JacksonStringParser(@NonNull JsonFactory jsonFactory, @NonNull Type type) {
        objectMapper = new ObjectMapper(jsonFactory);
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonStringParser(@NonNull ObjectMapper objectMapper, @NonNull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    @Nullable
    public Parsed call(@NonNull String source) {
        try {
            return objectMapper.readValue(source, parsedType);
        } catch (IOException e) {
            return null;
        }
    }
}
