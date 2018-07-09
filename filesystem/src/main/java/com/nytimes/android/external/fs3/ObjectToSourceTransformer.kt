package com.nytimes.android.external.fs3

import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import okio.BufferedSource

/**
 * This Transformer applies `map` to the source [io.reactivex.Single] in order to transform it from
 * one that emits objects of type `Parsed` to one that emits [okio.BufferedSource] of those objects.
 *
 *
 * @param <Parsed> the type of objects emitted by the source single.
</Parsed> */
class ObjectToSourceTransformer<Parsed>(protected var adapter: BufferedSourceAdapter<Parsed>) : SingleTransformer<Parsed, BufferedSource> {

    override fun apply(upstream: Single<Parsed>): SingleSource<BufferedSource> {
        return upstream.map { `object` -> adapter.toJson(`object`) }
    }
}
