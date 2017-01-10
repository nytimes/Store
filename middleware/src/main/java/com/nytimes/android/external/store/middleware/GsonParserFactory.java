package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.nytimes.android.external.store.base.Parser;

import java.io.Reader;
import java.lang.reflect.Type;

import okio.BufferedSource;

/**
 * Factory which returns various Gson {@link Parser} implementations.
 */
public class GsonParserFactory {

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link Gson} instance.
     */
    public static <T> Parser<Reader, T> createReaderParser(Gson gson, Type type) {
        if (gson == null) throw new IllegalArgumentException("gson cannot be null.");
        if (type == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonReaderParser<>(gson, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    public static <T> Parser<Reader, T> createReaderParser(Type type) {
        if (type == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonReaderParser<>(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Gson} instance.
     */
    public static <T> Parser<BufferedSource, T> createSourceParser(Gson gson, Type type) {
        if (gson == null) throw new IllegalArgumentException("gson cannot be null.");
        if (type == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonSourceParser<>(gson, type);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    public static <T> Parser<BufferedSource, T> createSourceParser(Type type) {
        if (type == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonSourceParser<>(new Gson(), type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link Gson} instance.
     */
    public static <T> Parser<String, T> createStringParser(Gson gson,Type type) {
        if (gson == null) throw new IllegalArgumentException("gson cannot be null.");
        if (type == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonStringParser<>(gson, type);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default {@link Gson} instance.
     */
    public static <T> Parser<String, T> createStringParser(Class<T> parsedClass) {
        if (parsedClass == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonStringParser<>(new Gson(), parsedClass);
    }

}
