package com.nytimes.android.external.store3.middleware.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.nytimes.android.external.fs3.ObjectToSourceTransformer
import com.nytimes.android.external.store3.annotations.Experimental

/**
 * Factory which returns Jackson [io.reactivex.SingleTransformer] implementations.
 */
object JacksonTransformerFactory {

    /**
     * Returns a new [ObjectToSourceTransformer], which uses a [JacksonBufferedSourceAdapter] to parse from
     * objects of the specified type to JSON using the provided
     * [ObjectMapper][com.fasterxml.jackson.databind.ObjectMapper] instance.
     */
    @Experimental
    fun <Parsed> createObjectToSourceTransformer(objectMapper: ObjectMapper): ObjectToSourceTransformer<Parsed> {
        return ObjectToSourceTransformer(JacksonBufferedSourceAdapter(objectMapper))
    }

}
