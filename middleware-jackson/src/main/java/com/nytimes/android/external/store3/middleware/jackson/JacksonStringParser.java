package com.nytimes.android.external.store3.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.util.ParserException;

import java.io.IOException;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.annotations.NonNull;

public class JacksonStringParser<Parsed> implements Parser<String, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    public JacksonStringParser(@Nonnull JsonFactory jsonFactory, @Nonnull Type type) {
        objectMapper = new ObjectMapper(jsonFactory).registerModule(new KotlinModule());
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonStringParser(@Nonnull ObjectMapper objectMapper, @Nonnull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    public Parsed apply(@NonNull String s) throws ParserException {
        try {
            return objectMapper.readValue(s, parsedType);
        } catch (IOException e) {
            throw new ParserException(e.getMessage(), e);
        }
    }
}
