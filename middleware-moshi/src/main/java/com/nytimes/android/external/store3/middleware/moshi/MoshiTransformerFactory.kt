package com.nytimes.android.external.store3.middleware.moshi

import com.nytimes.android.external.fs3.ObjectToSourceTransformer
import com.nytimes.android.external.store3.annotations.Experimental
import com.squareup.moshi.Moshi

import java.lang.reflect.Type

/**
 * Factory which returns Moshi [io.reactivex.SingleTransformer] implementations.
 */
object MoshiTransformerFactory {

    /**
     * Returns a new [ObjectToSourceTransformer], which uses a [MoshiBufferedSourceAdapter] to parse from
     * objects of the specified type.
     */
    @Experimental
    fun <Parsed> createObjectToSourceTransformer(type: Type): ObjectToSourceTransformer<Parsed> {
        return ObjectToSourceTransformer(MoshiBufferedSourceAdapter(Moshi.Builder().build(),
                type))
    }

}
