package com.nytimes.android.external.store3.base

import com.nytimes.android.external.store3.base.impl.Store

/**
 * 2 * this interface allows us to mark a [Store] as "internal", exposing methods for retrieving data
 * directly from memory or from disk.
 */
interface InternalStore<Parsed, Key> : Store<Parsed, Key> {
//    suspend fun memory(key: Key): Parsed?

    suspend fun disk(key: Key): Parsed?
}
