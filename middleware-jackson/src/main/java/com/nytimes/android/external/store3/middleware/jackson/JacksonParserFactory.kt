package com.nytimes.android.external.store3.middleware.jackson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nytimes.android.external.store3.base.Parser

import java.io.Reader
import java.lang.reflect.Type

import okio.BufferedSource

/**
 * Factory which returns various Jackson [Parser] implementations.
 */
object JacksonParserFactory {

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided [JsonFactory] instance.
     */
    fun <T> createStringParser(jsonFactory: JsonFactory, type: Type): Parser<String, T> {
        if (jsonFactory == null) {
            throw NullPointerException("jsonFactory cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return JacksonStringParser(jsonFactory, type)
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * the provided [ObjectMapper] instance.
     */
    fun <T> createStringParser(objectMapper: ObjectMapper, type: Type): Parser<String, T> {
        if (objectMapper == null) {
            throw NullPointerException("objectMapper cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return JacksonStringParser(objectMapper, type)
    }

    /**
     * Returns a new Parser which parses from a String to the specified type, using
     * a new default [ObjectMapper] instance.
     */
    fun <T> createStringParser(type: Class<T>): Parser<String, T> {
        return createStringParser(ObjectMapper().registerModule(KotlinModule()), type)
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * the provided [JsonFactory] instance.
     */
    fun <T> createSourceParser(jsonFactory: JsonFactory,
                               type: Type): Parser<BufferedSource, T> {
        if (jsonFactory == null) {
            throw NullPointerException("jsonFactory cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return JacksonSourceParser(jsonFactory, type)
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * the provided [ObjectMapper] instance.
     */
    fun <T> createSourceParser(objectMapper: ObjectMapper,
                               type: Type): Parser<BufferedSource, T> {
        if (objectMapper == null) {
            throw NullPointerException("objectMapper cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return JacksonSourceParser(objectMapper, type)
    }

    /**
     * Returns a new Parser which parses from [BufferedSource] to the specified type, using
     * a new default configured [ObjectMapper] instance.
     */
    fun <T> createSourceParser(type: Type): Parser<BufferedSource, T> {
        return createSourceParser(ObjectMapper().registerModule(KotlinModule()), type)
    }

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * the provided [JsonFactory] instance.
     */
    fun <T> createReaderParser(jsonFactory: JsonFactory,
                               type: Type): Parser<Reader, T> {
        if (jsonFactory == null) {
            throw NullPointerException("objectMapper cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return JacksonReaderParser(jsonFactory, type)
    }

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * the provided [ObjectMapper] instance.
     */
    fun <T> createReaderParser(objectMapper: ObjectMapper,
                               type: Type): Parser<Reader, T> {
        if (objectMapper == null) {
            throw NullPointerException("objectMapper cannot be null.")
        }
        if (type == null) {
            throw NullPointerException("type cannot be null.")
        }
        return JacksonReaderParser(objectMapper, type)
    }

    /**
     * Returns a new Parser which parses from [Reader] to the specified type, using
     * a new default configured [ObjectMapper] instance.
     */
    fun <T> createReaderParser(type: Type): Parser<Reader, T> {
        return createReaderParser(ObjectMapper().registerModule(KotlinModule()), type)
    }
}
