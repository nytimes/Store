package com.nytimes.android.external.store3.base.impl

import java.util.concurrent.TimeUnit

internal object StoreDefaults {

    /**
     * Default Cache TTL, can be overridden
     *
     * @return memory persister ttl
     */
    val cacheTTL: Long
        get() = TimeUnit.HOURS.toSeconds(24)

    /**
     * Default mem persister is 1, can be overridden otherwise
     *
     * @return memory persister size
     */
    val cacheSize: Long
        get() = 100

    val cacheTTLTimeUnit: TimeUnit
        get() = TimeUnit.SECONDS
}
