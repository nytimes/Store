package com.nytimes.android.external.store3.middleware.moshi

import com.nytimes.android.external.store3.base.Parser
import com.squareup.moshi.Moshi

import okio.BufferedSource

/**
 * Factory which returns various Moshi [Parser] implementations.
 */
object MoshiParserFactory {

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided [Moshi] instance.
     */
    inline  fun <reified T> createStringParser(moshi: Moshi): Parser<String, T> {
        if (moshi == null) {
            throw NullPointerException("moshi cannot be null.")
        }
        return MoshiStringParser(moshi, T::class.java)
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default [Moshi] instance.
     */
    inline fun <reified T> createStringParser(): Parser<String, T> {
        return createStringParser(Moshi.Builder().build())
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * the provided [Moshi] instance.
     */
    inline fun <reified T> createSourceParser(moshi: Moshi): Parser<BufferedSource, T> {
        if (moshi == null) {
            throw NullPointerException("moshi cannot be null.")
        }
        return MoshiSourceParser<T>(moshi, T::class.java)
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * a new default configured [Moshi] instance.
     */
    inline fun <reified T> createSourceParser(): Parser<BufferedSource, T> {
        return createSourceParser(Moshi.Builder().build())
    }
}
