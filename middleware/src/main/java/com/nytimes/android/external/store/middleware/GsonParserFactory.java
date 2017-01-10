package com.nytimes.android.external.store.middleware;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nytimes.android.external.store.base.Parser;

import java.io.Reader;

import okio.BufferedSource;

/**
 * Factory which returns various Gson {@link Parser} implementations.
 */
public class GsonParserFactory {

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * the provided {@link Gson} instance.
     */
    public static <T> Parser<Reader, T> createReaderParser(Gson gson, Class<T> parsedClass) {
        if (gson == null) throw new IllegalArgumentException("gson cannot be null.");
        if (parsedClass == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonReaderParser<>(gson, parsedClass);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    public static <T> Parser<Reader, T> createReaderParser(Class<T> parsedClass) {
        if (parsedClass == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonReaderParser<>(new Gson(), parsedClass);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Gson} instance.
     */
    public static <T> Parser<BufferedSource, T> createSourceParser(Gson gson, Class<T> parsedClass) {
        if (gson == null) throw new IllegalArgumentException("gson cannot be null.");
        if (parsedClass == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonSourceParser<>(gson, parsedClass);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    public static <T> Parser<BufferedSource, T> createSourceParser(Class<T> parsedClass) {
        if (parsedClass == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonSourceParser<>(new Gson(), parsedClass);
    }

    /**
     * Returns a new Parser which parses from {@link BufferedSource} to the specified type, using
     * the provided {@link Gson} instance.
     */
    public static <T> Parser<BufferedSource, T> createSourceListParser(Gson gson, TypeToken<T> parsedTypeToken) {
        if (gson == null) throw new IllegalArgumentException("gson cannot be null.");
        if (parsedTypeToken == null) throw new IllegalArgumentException("type token cannot be null.");
        return new GsonSourceListParser<>(gson, parsedTypeToken);
    }

    /**
     * Returns a new Parser which parses from {@link Reader} to the specified type, using
     * a new default configured {@link Gson} instance.
     */
    public static <T> Parser<BufferedSource, T> createSourceListParser(TypeToken<T> parsedTypeToken) {
        if (parsedTypeToken == null) throw new IllegalArgumentException("type token cannot be null.");
        return new GsonSourceListParser<>(new Gson(), parsedTypeToken);
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided {@link Gson} instance.
     */
    public static <T> Parser<String, T> createStringParser(Gson gson, Class<T> parsedClass) {
        if (gson == null) throw new IllegalArgumentException("gson cannot be null.");
        if (parsedClass == null) throw new IllegalArgumentException("parsedClass cannot be null.");
        return new GsonStringParser<>(gson, parsedClass);
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
