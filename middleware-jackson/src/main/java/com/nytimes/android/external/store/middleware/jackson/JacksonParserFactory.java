package com.nytimes.android.external.store.middleware.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nytimes.android.external.cache.Preconditions;
import com.nytimes.android.external.store.base.Parser;

import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.lang.reflect.Type;

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
    @NotNull
    public static <T> Parser<String, T> createStringParser(@NotNull JsonFactory jsonFactory, @NotNull Type type) {
        Preconditions.checkNotNull(jsonFactory, "jsonFactory cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new JacksonStringParser<>(jsonFactory, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link ObjectMapper} instance.
     */
    @NotNull
    public static <T> Parser<String, T> createStringParser(@NotNull ObjectMapper objectMapper, @NotNull Type type) {
        Preconditions.checkNotNull(objectMapper, "objectMapper cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new JacksonStringParser<>(objectMapper, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link ObjectMapper} instance.
     */
    @NotNull
    public static <T> Parser<String, T> createStringParser(@NotNull Class<T> type) {
        return createStringParser(new ObjectMapper(), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link JsonFactory} instance.
     */
    @NotNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NotNull JsonFactory jsonFactory,
                                                                   @NotNull Type type) {
        Preconditions.checkNotNull(jsonFactory, "jsonFactory cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new JacksonSourceParser<T>(jsonFactory, type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link ObjectMapper} instance.
     */
    @NotNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NotNull ObjectMapper objectMapper,
                                                                   @NotNull Type type) {
        Preconditions.checkNotNull(objectMapper, "objectMapper cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new JacksonSourceParser<T>(objectMapper, type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * a new default configured {@link ObjectMapper} instance.
     */
    @NotNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NotNull Type type) {
        return createSourceParser(new ObjectMapper(), type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link JsonFactory} instance.
     */
    @NotNull
    public static <T> Parser<Reader, T> createReaderParser(@NotNull JsonFactory jsonFactory,
                                                           @NotNull Type type) {
        Preconditions.checkNotNull(jsonFactory, "jsonFactory cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new JacksonReaderParser<>(jsonFactory, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link ObjectMapper} instance.
     */
    @NotNull
    public static <T> Parser<Reader, T> createReaderParser(@NotNull ObjectMapper objectMapper,
                                                           @NotNull Type type) {
        Preconditions.checkNotNull(objectMapper, "objectMapper cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new JacksonReaderParser<T>(objectMapper, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link ObjectMapper} instance.
     */
    @NotNull
    public static <T> Parser<Reader, T> createReaderParser(@NotNull Type type) {
        return createReaderParser(new ObjectMapper(), type);
    }
}
