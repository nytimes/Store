package com.nytimes.android.external.store3.middleware

import com.google.gson.Gson
import com.nytimes.android.external.fs3.BufferedSourceAdapter

import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import javax.inject.Inject

import okio.BufferedSource
import okio.Okio

/**
 * An implementation of [BufferedSourceAdapter] that uses
 * [Gson][com.google.gson.Gson] to convert Java values to JSON.
 */
class GsonBufferedSourceAdapter<Parsed> @Inject
constructor(private val gson: Gson) : BufferedSourceAdapter<Parsed> {

    override fun toJson(value: Parsed): BufferedSource {
        return Okio.buffer(Okio.source(ByteArrayInputStream(gson.toJson(value).toByteArray(
                Charset.forName("UTF-8")))))
    }
}
