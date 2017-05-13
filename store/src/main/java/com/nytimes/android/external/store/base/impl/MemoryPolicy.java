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

    private final long expireAfterWrite;
    private final long expireAfterAccess;
    private final TimeUnit expireAfterTimeUnit;
    private final long maxSize;

    MemoryPolicy(long expireAfterWrite, long expireAfterAccess, TimeUnit expireAfterTimeUnit, long maxSize) {
        this.expireAfterWrite = expireAfterWrite;
        this.expireAfterAccess = expireAfterAccess;
        this.expireAfterTimeUnit = expireAfterTimeUnit;
        this.maxSize = maxSize;
    }

    public static MemoryPolicyBuilder builder() {
        return new MemoryPolicyBuilder();
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public long getExpireAfterAccess() {
        return expireAfterAccess;
    }

    public TimeUnit getExpireAfterTimeUnit() {
        return expireAfterTimeUnit;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public boolean isDefaultPolicy() {
        return expireAfterWrite == -1;
    }

    public static class MemoryPolicyBuilder {
        private long expireAfterWrite = -1;
        private long expireAfterAccess = -1;
        private TimeUnit expireAfterTimeUnit = TimeUnit.SECONDS;
        private long maxSize = 1;

        public MemoryPolicyBuilder setExpireAfterWrite(long expireAfterWrite) {
            if (expireAfterAccess != -1) {
                throw new IllegalStateException("Cannot set expireAfterWrite with expireAfterAccess already set");
            }
            this.expireAfterWrite = expireAfterWrite;
            return this;
        }

        public MemoryPolicyBuilder setExpireAfterAccess(long expireAfterAccess) {
            if (expireAfterWrite != -1) {
                throw new IllegalStateException("Cannot set expireAfterAccess with expireAfterWrite already set");
            }
            this.expireAfterAccess = expireAfterAccess;
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

            return new MemoryPolicy(expireAfterWrite, expireAfterAccess, expireAfterTimeUnit, maxSize);
        }
    }
}
