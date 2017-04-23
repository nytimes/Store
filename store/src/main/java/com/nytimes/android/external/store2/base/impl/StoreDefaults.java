package com.nytimes.android.external.store2.base.impl;

import java.util.concurrent.TimeUnit;

final class StoreDefaults {
    private StoreDefaults() {

    }

    /**
     * Default Cache TTL, can be overridden
     *
     * @return memory persister ttl
     */
    static long getCacheTTL() {
        return TimeUnit.HOURS.toSeconds(24);
    }

    /**
     * Default mem persister is 1, can be overridden otherwise
     *
     * @return memory persister size
     */
    static long getCacheSize() {
        return 100;
    }

    static TimeUnit getCacheTTLTimeUnit() {
        return TimeUnit.SECONDS;
    }
}
