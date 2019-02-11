package com.nytimes.android.external.store3.middleware

import com.google.gson.Gson
import com.nytimes.android.external.fs3.ObjectToSourceTransformer
import com.nytimes.android.external.store3.annotations.Experimental

/**
 * Factory which returns Gson [io.reactivex.SingleTransformer] implementations.
 */
object GsonTransformerFactory {

    /**
     * Returns a new [ObjectToSourceTransformer], which uses a [GsonBufferedSourceAdapter] to parse from
     * objects of the specified type to JSON using the provided [Gson] instance.
     */
    @Experimental
    fun <Parsed> createObjectToSourceTransformer(gson: Gson): ObjectToSourceTransformer<Parsed> = ObjectToSourceTransformer(GsonBufferedSourceAdapter(gson))

}
