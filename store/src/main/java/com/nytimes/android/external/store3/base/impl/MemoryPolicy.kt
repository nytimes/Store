package com.nytimes.android.external.store3.base.impl


import com.nytimes.android.external.store3.util.NoopPersister

import java.util.concurrent.TimeUnit

/**
 * MemoryPolicy holds all required info to create MemoryCache and
 * [NoopPersister]
 *
 *
 * This class is used, in order to define the appropriate parameters for the MemoryCache
 * to be built.
 *
 *
 * MemoryPolicy is used by a [Store]
 * and defines the in-memory cache behavior. It is also used by
 * [NoopPersister]
 * to define a basic caching mechanism.
 */
class MemoryPolicy internal constructor(
        val expireAfterWrite: Long,
        val expireAfterAccess: Long,
        val expireAfterTimeUnit: TimeUnit,
        private val maxSizeNotDefault: Long
) {

    val isDefaultWritePolicy: Boolean
        get() = expireAfterWrite == DEFAULT_POLICY

    val isDefaultAccessPolicy: Boolean
        get() = expireAfterAccess == DEFAULT_POLICY

    val isDefaultMaxSize: Boolean
        get() = maxSizeNotDefault == DEFAULT_POLICY

    val maxSize get(): Long = if (isDefaultMaxSize) 1 else maxSizeNotDefault

    fun hasWritePolicy() = expireAfterWrite != DEFAULT_POLICY

    fun hasAccessPolicy() = expireAfterAccess != DEFAULT_POLICY

    fun hasMaxSize() = maxSize != DEFAULT_POLICY

    class MemoryPolicyBuilder {
        private var expireAfterWrite = DEFAULT_POLICY
        private var expireAfterAccess = DEFAULT_POLICY
        private var expireAfterTimeUnit = TimeUnit.SECONDS
        private var maxSize: Long = -1

        fun setExpireAfterWrite(expireAfterWrite: Long): MemoryPolicyBuilder = apply {
            if (expireAfterAccess != DEFAULT_POLICY) {
                throw IllegalStateException("Cannot set expireAfterWrite with expireAfterAccess already set")
            }
            this.expireAfterWrite = expireAfterWrite
        }

        fun setExpireAfterAccess(expireAfterAccess: Long): MemoryPolicyBuilder = apply {
            if (expireAfterWrite != DEFAULT_POLICY) {
                throw IllegalStateException("Cannot set expireAfterAccess with expireAfterWrite already set")
            }
            this.expireAfterAccess = expireAfterAccess
        }

        fun setExpireAfterTimeUnit(expireAfterTimeUnit: TimeUnit): MemoryPolicyBuilder = apply {
            this.expireAfterTimeUnit = expireAfterTimeUnit
        }

        fun setMemorySize(maxSize: Long): MemoryPolicyBuilder = apply {
            this.maxSize = maxSize
        }

        fun build() = MemoryPolicy(expireAfterWrite, expireAfterAccess, expireAfterTimeUnit, maxSize)
    }

    companion object {

        val DEFAULT_POLICY: Long = -1

        fun builder(): MemoryPolicyBuilder {
            return MemoryPolicyBuilder()
        }
    }
}
