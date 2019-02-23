package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.cache3.Cache
import com.nytimes.android.external.cache3.CacheBuilder
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

object CacheFactory {

    internal fun <Key, Parsed> createCache(memoryPolicy: MemoryPolicy?): Cache<Key, Parsed> {
        return createBaseCache(memoryPolicy)
    }

    internal fun <Key, Parsed> createInflighter(memoryPolicy: MemoryPolicy?): Cache<Key, Parsed> {
        return createBaseInFlighter(memoryPolicy)
    }

    @JvmStatic
    fun <Key, Parsed> createRoomCache(memoryPolicy: MemoryPolicy): Cache<Key, Observable<Parsed>> {
        return createBaseCache(memoryPolicy)
    }

    @JvmStatic
    fun <Key, Parsed> createRoomInflighter(memoryPolicy: MemoryPolicy): Cache<Key, Observable<Parsed>> {
        return createBaseInFlighter(memoryPolicy)
    }

    private fun <Key, Value> createBaseInFlighter(memoryPolicy: MemoryPolicy?): Cache<Key, Value> {
        val expireAfterToSeconds = memoryPolicy?.expireAfterTimeUnit?.toSeconds(memoryPolicy.expireAfterWrite)
                ?: StoreDefaults.getCacheTTLTimeUnit()
                        .toSeconds(StoreDefaults.getCacheTTL())
        val maximumInFlightRequestsDuration = TimeUnit.MINUTES.toSeconds(1)

        return if (expireAfterToSeconds > maximumInFlightRequestsDuration) {
            CacheBuilder
                    .newBuilder()
                    .expireAfterWrite(maximumInFlightRequestsDuration, TimeUnit.SECONDS)
                    .build()
        } else {
            val expireAfter = memoryPolicy?.expireAfterWrite ?: StoreDefaults.getCacheTTL()
            val expireAfterUnit = if (memoryPolicy == null)
                StoreDefaults.getCacheTTLTimeUnit()
            else
                memoryPolicy.expireAfterTimeUnit
            CacheBuilder.newBuilder()
                    .expireAfterWrite(expireAfter, expireAfterUnit)
                    .build()
        }
    }


    private fun <Key, Value> createBaseCache(memoryPolicy: MemoryPolicy?): Cache<Key, Value> {
        return if (memoryPolicy == null) {
            CacheBuilder
                    .newBuilder()
                    .maximumSize(StoreDefaults.getCacheSize())
                    .expireAfterWrite(StoreDefaults.getCacheTTL(), StoreDefaults.getCacheTTLTimeUnit())
                    .build()
        } else {
            if (memoryPolicy.expireAfterAccess == MemoryPolicy.DEFAULT_POLICY) {
                CacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.maxSize)
                        .expireAfterWrite(memoryPolicy.expireAfterWrite, memoryPolicy.expireAfterTimeUnit)
                        .build()
            } else {
                CacheBuilder
                        .newBuilder()
                        .maximumSize(memoryPolicy.maxSize)
                        .expireAfterAccess(memoryPolicy.expireAfterAccess, memoryPolicy.expireAfterTimeUnit)
                        .build()
            }
        }
    }

}
