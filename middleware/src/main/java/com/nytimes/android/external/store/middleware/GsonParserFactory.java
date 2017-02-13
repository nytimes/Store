package com.nytimes.android.external.store.middleware;


import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import java.io.Reader;
import java.lang.reflect.Type;

import javax.annotation.Nonnull;

import okio.BufferedSource;

/**
 * Factory which returns various Gson {@link Parser} implementations.
 */
public final class GsonParserFactory {
    private GsonParserFactory() {
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    @Nonnull
    public static <T> Parser<Reader, T> createReaderParser(@Nonnull Type type) {
        return createReaderParser(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link Gson} instance.
     */
    @Nonnull
    public static <T> Parser<Reader, T> createReaderParser(@Nonnull Gson gson, @Nonnull Type type) {
        return new GsonReaderParser<>(gson, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    @Nonnull
    public static <T> Parser<BufferedSource, T> createSourceParser(@Nonnull Type type) {
        return createSourceParser(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Gson} instance.
     */
    @Nonnull
    public static <T> Parser<BufferedSource, T> createSourceParser(@Nonnull Gson gson, @Nonnull Type type) {
        return new GsonSourceParser<>(gson, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link Gson} instance.
     */
    @Nonnull
    public static <T> Parser<String, T> createStringParser(@Nonnull Class<T> type) {
        return createStringParser(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link Gson} instance.
     */
    @Nonnull
    public static <T> Parser<String, T> createStringParser(@Nonnull Gson gson, @Nonnull Type type) {
        return new GsonStringParser<>(gson, type);
    }

}
