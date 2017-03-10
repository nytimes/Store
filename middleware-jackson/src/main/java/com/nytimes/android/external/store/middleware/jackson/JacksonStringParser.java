package com.nytimes.android.external.store.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store.base.Parser;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

public class JacksonStringParser<Parsed> implements Parser<String, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    public JacksonStringParser(@Nonnull JsonFactory jsonFactory, @Nonnull Type type) {
        objectMapper = new ObjectMapper(jsonFactory);
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonStringParser(@Nonnull ObjectMapper objectMapper, @Nonnull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    @SuppressWarnings({"PMD.EmptyCatchBlock", "PMD.SignatureDeclareThrowsException"})
    public Parsed apply(@NonNull String s) throws Exception {
        try {
            return objectMapper.readValue(s, parsedType);
        } catch (IOException e) {
            return null;
        }
    }
}
