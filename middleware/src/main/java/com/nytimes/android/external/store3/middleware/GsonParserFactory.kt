package com.nytimes.android.external.store3.middleware


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nytimes.android.external.store3.base.Parser

import java.io.Reader

import okio.BufferedSource

/**
 * Factory which returns various Gson [Parser] implementations.
 */
object GsonParserFactory {

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * a new default configured [Gson] instance.
     */
    inline fun <reified T> createReaderParser(): Parser<Reader, T> = createReaderParser(Gson())

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * the provided [Gson] instance.
     */

    inline fun <reified T> createReaderParser(gson: Gson): Parser<Reader, T> = GsonReaderParser(gson, object : TypeToken<T>() {}.type)

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * a new default configured [Gson] instance.
     */
    inline fun <reified T> createSourceParser(): Parser<BufferedSource, T> = createSourceParser(Gson())

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * the provided [Gson] instance.
     */
    inline fun <reified T> createSourceParser(gson: Gson): Parser<BufferedSource, T> = GsonSourceParser(gson, object : TypeToken<T>() {}.type)

    /*
    object : TypeToken<T>() {

        }.type)
     */

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default [Gson] instance.
     */
    inline fun <reified T> createStringParser(): Parser<String, T> = createStringParser(Gson())

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided [Gson] instance.
     */
    inline fun <reified T>  createStringParser(gson: Gson): Parser<String, T> = GsonStringParser(gson, object : TypeToken<T>() {}.type)

}
