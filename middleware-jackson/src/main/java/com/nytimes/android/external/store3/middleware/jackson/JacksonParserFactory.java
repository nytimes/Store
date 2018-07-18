package com.nytimes.android.external.store3.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.nytimes.android.external.store3.base.Parser;

import java.io.Reader;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;

import okio.BufferedSource;

/**
 * Factory which returns various Jackson {@link Parser} implementations.
 */
public final class JacksonParserFactory {

    private JacksonParserFactory() {
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link JsonFactory} instance.
     */
    @Nonnull
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T> Parser<String, T> createStringParser(@Nonnull JsonFactory jsonFactory, @Nonnull Type type) {
        if (jsonFactory == null) {
            throw new NullPointerException("jsonFactory cannot be null.");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        return new JacksonStringParser<>(jsonFactory, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link ObjectMapper} instance.
     */
    @Nonnull
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T> Parser<String, T> createStringParser(@Nonnull ObjectMapper objectMapper, @Nonnull Type type) {
        if (objectMapper == null) {
            throw new NullPointerException("objectMapper cannot be null.");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        return new JacksonStringParser<>(objectMapper, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link ObjectMapper} instance.
     */
    @Nonnull
    public static <T> Parser<String, T> createStringParser(@Nonnull Class<T> type) {
        return createStringParser(new ObjectMapper().registerModule(new KotlinModule()), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link JsonFactory} instance.
     */
    @Nonnull
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T> Parser<BufferedSource, T> createSourceParser(@Nonnull JsonFactory jsonFactory,
                                                                   @Nonnull Type type) {
        if (jsonFactory == null) {
            throw new NullPointerException("jsonFactory cannot be null.");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        return new JacksonSourceParser<>(jsonFactory, type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link ObjectMapper} instance.
     */
    @Nonnull
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T> Parser<BufferedSource, T> createSourceParser(@Nonnull ObjectMapper objectMapper,
                                                                   @Nonnull Type type) {
        if (objectMapper == null) {
            throw new NullPointerException("objectMapper cannot be null.");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        return new JacksonSourceParser<>(objectMapper, type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * a new default configured {@link ObjectMapper} instance.
     */
    @Nonnull
    public static <T> Parser<BufferedSource, T> createSourceParser(@Nonnull Type type) {
        return createSourceParser(new ObjectMapper().registerModule(new KotlinModule()), type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link JsonFactory} instance.
     */
    @Nonnull
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T> Parser<Reader, T> createReaderParser(@Nonnull JsonFactory jsonFactory,
                                                           @Nonnull Type type) {
        if (jsonFactory == null) {
            throw new NullPointerException("objectMapper cannot be null.");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        return new JacksonReaderParser<>(jsonFactory, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link ObjectMapper} instance.
     */
    @Nonnull
    @SuppressWarnings("PMD.AvoidThrowingNullPointerException")
    public static <T> Parser<Reader, T> createReaderParser(@Nonnull ObjectMapper objectMapper,
                                                           @Nonnull Type type) {
        if (objectMapper == null) {
            throw new NullPointerException("objectMapper cannot be null.");
        }
        if (type == null) {
            throw new NullPointerException("type cannot be null.");
        }
        return new JacksonReaderParser<>(objectMapper, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link ObjectMapper} instance.
     */
    @Nonnull
    public static <T> Parser<Reader, T> createReaderParser(@Nonnull Type type) {
        return createReaderParser(new ObjectMapper().registerModule(new KotlinModule()), type);
    }
}
