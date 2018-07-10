package com.nytimes.android.external.store3.middleware.moshi

import com.nytimes.android.external.fs3.BufferedSourceAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject
import okio.Buffer
import okio.BufferedSource

/**
 * An implementation of [BufferedSourceAdapter] that uses
 * [Moshi] to convert Java values to JSON.
 */
class MoshiBufferedSourceAdapter<Parsed> @Inject
constructor(moshi: Moshi, type: Type) : BufferedSourceAdapter<Parsed> {

    private val jsonAdapter: JsonAdapter<Parsed>

    init {
        this.jsonAdapter = moshi.adapter(type)
    }

    override fun toJson(value: Parsed): BufferedSource {
        val buffer = Buffer()
        try {
            jsonAdapter.toJson(buffer, value)
        } catch (e: IOException) {
            throw AssertionError(e) // No I/O writing to a Buffer.
        }

        return buffer
    }
}
