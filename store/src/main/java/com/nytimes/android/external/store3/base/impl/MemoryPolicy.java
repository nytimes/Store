package com.nytimes.android.external.store3.base.impl;


import com.nytimes.android.external.store3.util.NoopPersister;

import java.util.concurrent.TimeUnit;

/**
 * MemoryPolicy holds all required info to create MemoryCache and
 * {@link NoopPersister NoopPersister}
 * <p>
 * This class is used, in order to define the appropriate parameters for the MemoryCache
 * to be built.
 * <p>
 * MemoryPolicy is used by a {@link Store Store}
 * and defines the in-memory cache behavior. It is also used by
 * {@link NoopPersister NoopPersister}
 * to define a basic caching mechanism.
 */
public class MemoryPolicy {

    public static final long DEFAULT_POLICY = -1;
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

    /**
     * @deprecated Use {@link MemoryPolicy#getExpireAfterWrite()} or {@link MemoryPolicy#getExpireAfterAccess()}.
     */
    @Deprecated
    public long getExpireAfter() {
        return expireAfterWrite;
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
        if (isDefaultMaxSize()) {
            return 1;
        }
        return maxSize;
    }

    /**
     * @deprecated Use {@link MemoryPolicy#isDefaultWritePolicy()} or {@link MemoryPolicy#isDefaultAccessPolicy()}.
     */
    @Deprecated
    public boolean isDefaultPolicy() {
        return expireAfterWrite == DEFAULT_POLICY;
    }

    public boolean isDefaultWritePolicy() {
        return expireAfterWrite == DEFAULT_POLICY;
    }

    public boolean isDefaultAccessPolicy() {
        return expireAfterAccess == DEFAULT_POLICY;
    }

    public boolean isDefaultMaxSize() {
        return maxSize == DEFAULT_POLICY;
    }

    public boolean hasWritePolicy() {
        return expireAfterWrite != DEFAULT_POLICY;
    }

    public boolean hasAccessPolicy() {
        return expireAfterAccess != DEFAULT_POLICY;
    }

    public boolean hasMaxSize() {
        return maxSize != DEFAULT_POLICY;
    }

    public static class MemoryPolicyBuilder {
        private long expireAfterWrite = DEFAULT_POLICY;
        private long expireAfterAccess = DEFAULT_POLICY;
        private TimeUnit expireAfterTimeUnit = TimeUnit.SECONDS;
        private long maxSize = -1;

        /**
         * @deprecated Use {@link MemoryPolicyBuilder#setExpireAfterWrite(long)} or
         * {@link MemoryPolicyBuilder#setExpireAfterAccess(long)}.
         */
        @Deprecated
        public MemoryPolicyBuilder setExpireAfter(long expireAfter) {
            return setExpireAfterWrite(expireAfter);
        }

        public MemoryPolicyBuilder setExpireAfterWrite(long expireAfterWrite) {
            if (expireAfterAccess != DEFAULT_POLICY) {
                throw new IllegalStateException("Cannot set expireAfterWrite with expireAfterAccess already set");
            }
            this.expireAfterWrite = expireAfterWrite;
            return this;
        }

        public MemoryPolicyBuilder setExpireAfterAccess(long expireAfterAccess) {
            if (expireAfterWrite != DEFAULT_POLICY) {
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
