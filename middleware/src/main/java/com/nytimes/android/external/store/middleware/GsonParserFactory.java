package com.nytimes.android.external.store.middleware;


import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.lang.reflect.Type;

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
    @NotNull
    public static <T> Parser<Reader, T> createReaderParser(@NotNull Type type) {
        return createReaderParser(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link Gson} instance.
     */
    @NotNull
    public static <T> Parser<Reader, T> createReaderParser(@NotNull Gson gson, @NotNull Type type) {
        return new GsonReaderParser<>(gson, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    @NotNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NotNull Type type) {
        return createSourceParser(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Gson} instance.
     */
    @NotNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NotNull Gson gson, @NotNull Type type) {
        return new GsonSourceParser<>(gson, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link Gson} instance.
     */
    @NotNull
    public static <T> Parser<String, T> createStringParser(@NotNull Class<T> type) {
        return createStringParser(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link Gson} instance.
     */
    @NotNull
    public static <T> Parser<String, T> createStringParser(@NotNull Gson gson, @NotNull Type type) {
        return new GsonStringParser<>(gson, type);
    }

}
