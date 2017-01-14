package com.nytimes.android.external.store.middleware.moshi;

import android.support.annotation.NonNull;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.Moshi;

import java.io.Reader;
import java.lang.reflect.Type;

import okio.BufferedSource;

/**
 * Factory which returns various Moshi {@link Parser} implementations.
 */
public final class MoshiParserFactory {

    private MoshiParserFactory() {
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link Moshi} instance.
     */
    @NonNull
    public static <T> Parser<String, T> createStringParser(@NonNull Moshi moshi, @NonNull Type type) {
        if (moshi == null) {
            throw new IllegalArgumentException("moshi cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        return new MoshiStringParser<>(moshi, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link Moshi} instance.
     */
    @NonNull
    public static <T> Parser<String, T> createStringParser(@NonNull Class<T> type) {
        return createStringParser(new Moshi.Builder().build(), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Moshi} instance.
     */
    @NonNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NonNull Moshi moshi, @NonNull Type type) {
        if (moshi == null) {
            throw new IllegalArgumentException("moshi cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        return new MoshiSourceParser<>(moshi, type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * a new default configured {@link Moshi} instance.
     */
    @NonNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NonNull Type type) {
        return createSourceParser(new Moshi.Builder().build(), type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link Moshi} instance.
     */
    @NonNull
    public static <T> Parser<Reader, T> createReaderParser(@NonNull Moshi moshi, @NonNull Type type) {
        if (moshi == null) {
            throw new IllegalArgumentException("moshi cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null.");
        }
        return new MoshiReaderParser<>(moshi, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Moshi} instance.
     */
    @NonNull
    public static <T> Parser<Reader, T> createReaderParser(@NonNull Type type) {
        return createReaderParser(new Moshi.Builder().build(), type);
    }
}
