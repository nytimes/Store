package com.nytimes.android.external.store3.middleware.moshi

import com.nytimes.android.external.store3.base.Parser
import com.squareup.moshi.Moshi

import java.lang.reflect.Type

import okio.BufferedSource

/**
 * Factory which returns various Moshi [Parser] implementations.
 */
object MoshiParserFactory {

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided [Moshi] instance.
     */
    fun <T> createStringParser(moshi: Moshi, type: Type): Parser<String, T> {
        if (moshi == null) {
            throw NullPointerException("moshi cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return MoshiStringParser(moshi, type)
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default [Moshi] instance.
     */
    fun <T> createStringParser(type: Class<T>): Parser<String, T> {
        return createStringParser(Moshi.Builder().build(), type)
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * the provided [Moshi] instance.
     */
    fun <T> createSourceParser(moshi: Moshi, type: Type): Parser<BufferedSource, T> {
        if (moshi == null) {
            throw NullPointerException("moshi cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return MoshiSourceParser(moshi, type)
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * a new default configured [Moshi] instance.
     */
    fun <T> createSourceParser(type: Type): Parser<BufferedSource, T> {
        return createSourceParser(Moshi.Builder().build(), type)
    }
}
