package com.nytimes.android.external.store.middleware.jackson;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.store.base.Parser;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class JacksonReaderParser<Parsed> implements Parser<Reader, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    public JacksonReaderParser(@Nonnull JsonFactory jsonFactory, @Nonnull Type type) {
        objectMapper = new ObjectMapper(jsonFactory);
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonReaderParser(@Nonnull ObjectMapper objectMapper, @Nonnull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    public Parsed call(@Nonnull Reader reader) {
        try {
            return objectMapper.readValue(reader, parsedType);
        } catch (IOException e) {
            return null;
        }
    }
}
