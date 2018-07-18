package com.nytimes.android.external.store3.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.nytimes.android.external.store3.base.Parser;
import com.nytimes.android.external.store3.util.ParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import io.reactivex.annotations.NonNull;
import okio.BufferedSource;

public class JacksonSourceParser<Parsed> implements Parser<BufferedSource, Parsed> {

    private final ObjectMapper objectMapper;
    private final JavaType parsedType;

    public JacksonSourceParser(@Nonnull JsonFactory jsonFactory, @Nonnull Type type) {
        objectMapper = new ObjectMapper(jsonFactory).registerModule(new KotlinModule());
        parsedType = objectMapper.constructType(type);
    }

    @Inject
    public JacksonSourceParser(@Nonnull ObjectMapper objectMapper, @Nonnull Type type) {
        this.objectMapper = objectMapper;
        parsedType = objectMapper.constructType(type);
    }

    @Override
    @SuppressWarnings({"PMD.EmptyCatchBlock"})
    public Parsed apply(@NonNull BufferedSource bufferedSource) throws ParserException {
        InputStream inputStream = bufferedSource.inputStream();
        try {
            return objectMapper.readValue(inputStream, parsedType);
        } catch (IOException e) {
            throw new ParserException(e.getMessage(), e);
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
