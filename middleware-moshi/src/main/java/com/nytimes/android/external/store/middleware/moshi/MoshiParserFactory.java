package com.nytimes.android.external.store.middleware.moshi;

import com.nytimes.android.external.cache.Preconditions;
import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;

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
    @NotNull
    public static <T> Parser<String, T> createStringParser(@NotNull Moshi moshi, @NotNull Type type) {
        Preconditions.checkNotNull(moshi, "moshi cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new MoshiStringParser<>(moshi, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link Moshi} instance.
     */
    @NotNull
    public static <T> Parser<String, T> createStringParser(@NotNull Class<T> type) {
        return createStringParser(new Moshi.Builder().build(), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Moshi} instance.
     */
    @NotNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NotNull Moshi moshi, @NotNull Type type) {
        Preconditions.checkNotNull(moshi, "moshi cannot be null.");
        Preconditions.checkNotNull(type, "type cannot be null.");
        return new MoshiSourceParser<>(moshi, type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * a new default configured {@link Moshi} instance.
     */
    @NotNull
    public static <T> Parser<BufferedSource, T> createSourceParser(@NotNull Type type) {
        return createSourceParser(new Moshi.Builder().build(), type);
    }
}
