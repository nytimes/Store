package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.annotations.Experimental

/**
 * Wraps methods for fluent MemoryPolicy instantiation.
 */
@Experimental
class FluentMemoryPolicyBuilder private constructor() {
    companion object {
        /**
         * Builds a MemoryPolicy.
         * @param config The desired configuration for the memory policy.
         * @return A MemoryPolicy with the desired parameters or default ones if none were provided.
         */
        fun build(config: (MemoryPolicyParameters.() -> Unit)? = null): MemoryPolicy =
            MemoryPolicyParameters().apply { config?.invoke(this) }.let {
                MemoryPolicy.builder()
                        .apply {
                            if (it.expireAfterAccess != MemoryPolicy.DEFAULT_POLICY) {
                                this.setExpireAfterAccess(it.expireAfterAccess)
                            } else if (it.expireAfterWrite != MemoryPolicy.DEFAULT_POLICY) {
                                this.setExpireAfterWrite(it.expireAfterWrite)
                            }
                        }
                        .setExpireAfterTimeUnit(it.expireAfterTimeUnit)
                        .setMemorySize(it.memorySize)
                        .build()
            }
    }
}
