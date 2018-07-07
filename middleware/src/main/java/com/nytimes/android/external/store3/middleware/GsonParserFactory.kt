package com.nytimes.android.external.store3.middleware


import com.google.gson.Gson
import com.nytimes.android.external.store3.base.Parser

import java.io.Reader
import java.lang.reflect.Type

import okio.BufferedSource

/**
 * Factory which returns various Gson [Parser] implementations.
 */
object GsonParserFactory {

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * a new default configured [Gson] instance.
     */
    fun <T> createReaderParser(type: Type): Parser<Reader, T> {
        return createReaderParser(Gson(), type)
    }

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * the provided [Gson] instance.
     */
    fun <T> createReaderParser(gson: Gson, type: Type): Parser<Reader, T> {
        return GsonReaderParser(gson, type)
    }

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * a new default configured [Gson] instance.
     */
    fun <T> createSourceParser(type: Type): Parser<BufferedSource, T> {
        return createSourceParser(Gson(), type)
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * the provided [Gson] instance.
     */
    fun <T> createSourceParser(gson: Gson, type: Type): Parser<BufferedSource, T> {
        return GsonSourceParser(gson, type)
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default [Gson] instance.
     */
    fun <T> createStringParser(type: Class<T>): Parser<String, T> {
        return createStringParser(Gson(), type)
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided [Gson] instance.
     */
    fun <T> createStringParser(gson: Gson, type: Type): Parser<String, T> {
        return GsonStringParser(gson, type)
    }

}
