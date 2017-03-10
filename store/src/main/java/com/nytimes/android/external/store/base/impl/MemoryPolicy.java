package com.nytimes.android.external.store.base.impl;


import java.util.concurrent.TimeUnit;

/**
 * MemoryPolicy holds all required info to create MemoryCache and
 * {@link com.nytimes.android.external.store.util.NoopPersister NoopPersister}
 * <p>
 * This class is used, in order to define the appropriate parameters for the MemoryCache
 * to be built.
 * <p>
 * MemoryPolicy is used by a {@link com.nytimes.android.external.store.base.impl.Store Store}
 * and defines the in-memory cache behavior. It is also used by
 * {@link com.nytimes.android.external.store.util.NoopPersister NoopPersister}
 * to define a basic caching mechanism.
 */
public class MemoryPolicy {

    private final long expireAfter;
    private final TimeUnit expireAfterTimeUnit;
    private final long maxSize;

    MemoryPolicy(long expireAfter, TimeUnit expireAfterTimeUnit, long maxSize) {
        this.expireAfter = expireAfter;
        this.expireAfterTimeUnit = expireAfterTimeUnit;
        this.maxSize = maxSize;
    }

    public static MemoryPolicyBuilder builder() {
        return new MemoryPolicyBuilder();
    }

    public long getExpireAfter() {
        return expireAfter;
    }

    public TimeUnit getExpireAfterTimeUnit() {
        return expireAfterTimeUnit;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public boolean isDefaultPolicy() {
        return expireAfter == -1;
    }

    public static class MemoryPolicyBuilder {
        private long expireAfter = -1;
        private TimeUnit expireAfterTimeUnit = TimeUnit.SECONDS;
        private long maxSize = 1;

        public MemoryPolicyBuilder setExpireAfter(long expireAfter) {
            this.expireAfter = expireAfter;
            return this;
        }

        public MemoryPolicyBuilder setExpireAfterTimeUnit(TimeUnit expireAfterTimeUnit) {
            this.expireAfterTimeUnit = expireAfterTimeUnit;
            return this;
        }

        public MemoryPolicyBuilder setMemorySize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public MemoryPolicy build() {
            return new MemoryPolicy(expireAfter, expireAfterTimeUnit, maxSize);
        }
    }
}
