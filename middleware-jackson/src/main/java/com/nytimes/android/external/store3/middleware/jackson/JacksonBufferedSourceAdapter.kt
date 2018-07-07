package com.nytimes.android.external.store3.middleware.jackson

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.nytimes.android.external.fs3.BufferedSourceAdapter

import java.io.ByteArrayInputStream
import javax.inject.Inject

import io.reactivex.exceptions.Exceptions
import okio.BufferedSource
import okio.Okio

/**
 * An implementation of [BufferedSourceAdapter] that uses [ObjectMapper] to convert Java values to JSON.
 */
class JacksonBufferedSourceAdapter<Parsed> @Inject
constructor(private val objectMapper: ObjectMapper) : BufferedSourceAdapter<Parsed> {

    override fun toJson(value: Parsed): BufferedSource {
        try {
            return Okio.buffer(Okio.source(ByteArrayInputStream(objectMapper.writeValueAsBytes(value))))
        } catch (e: JsonProcessingException) {
            throw Exceptions.propagate(e)
        }

    }
}
