package com.nytimes.android.external.store3.middleware.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.nytimes.android.external.fs3.BufferedSourceAdapter
import okio.BufferedSource
import okio.Okio
import java.io.ByteArrayInputStream
import javax.inject.Inject

/**
 * An implementation of [BufferedSourceAdapter] that uses [ObjectMapper] to convert Java values to JSON.
 */
class JacksonBufferedSourceAdapter<Parsed> @Inject
constructor(private val objectMapper: ObjectMapper) : BufferedSourceAdapter<Parsed> {
    override fun toJson(value: Parsed): BufferedSource = Okio.buffer(Okio.source(ByteArrayInputStream(objectMapper.writeValueAsBytes(value))))
}
