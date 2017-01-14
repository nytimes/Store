package com.nytimes.android.external.store.middleware.moshi;

import android.support.annotation.NonNull;

import com.nytimes.android.external.store.base.Parser;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

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
            throw new IllegalArgumentException("moshi cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
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
}
