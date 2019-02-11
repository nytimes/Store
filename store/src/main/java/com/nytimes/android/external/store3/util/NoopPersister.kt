package com.nytimes.android.external.store3.util

import com.nytimes.android.external.cache3.Cache
import com.nytimes.android.external.cache3.CacheBuilder
import com.nytimes.android.external.store3.base.Clearable
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.MemoryPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import java.util.concurrent.TimeUnit

/**
 * Pass-through diskdao for stores that don't want to use persister
 */
class NoopPersister<Raw, Key> internal constructor(memoryPolicy: MemoryPolicy) : Persister<Raw, Key>, Clearable<Key> {
    val networkResponses: Cache<Key, Deferred<Raw>>
    private val memoryScope = CoroutineScope(SupervisorJob())

    init {
        if (memoryPolicy.hasAccessPolicy()) {
            networkResponses = CacheBuilder.newBuilder()
                    .expireAfterAccess(memoryPolicy.expireAfterAccess, memoryPolicy.expireAfterTimeUnit)
                    .build()

        } else if (memoryPolicy.hasWritePolicy()) {
            networkResponses = CacheBuilder.newBuilder()
                    .expireAfterWrite(memoryPolicy.expireAfterWrite, memoryPolicy.expireAfterTimeUnit)
                    .build()
        } else {
            throw IllegalArgumentException("No expiry policy set on memory-policy.")
        }
    }

    override suspend fun read(key: Key): Raw? = networkResponses.getIfPresent(key)?.await()


    override suspend fun write(key: Key, raw: Raw): Boolean {
        networkResponses.put(key, memoryScope.async { raw })
        return true
    }

    override fun clear(key: Key) {
        networkResponses.invalidate(key)
    }

    companion object {

        fun <Raw, Key> create(): NoopPersister<Raw, Key> {
            return NoopPersister.create(null)
        }

        fun <Raw, Key> create(memoryPolicy: MemoryPolicy?): NoopPersister<Raw, Key> {
            if (memoryPolicy == null) {
                val defaultPolicy = MemoryPolicy
                        .builder()
                        .setExpireAfterWrite(24)
                        .setExpireAfterTimeUnit(TimeUnit.HOURS)
                        .build()
                return NoopPersister(defaultPolicy)
            }
            return NoopPersister(memoryPolicy)
        }
    }
}
