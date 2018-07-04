package com.nytimes.android.external.store3.storecache;


import javax.annotation.Nonnull;

public class StoreCacheBuilder<K, V> {

    static final int UNSET_INT = -1;
    long maximumSize = UNSET_INT;

    @Nonnull
    public static StoreCacheBuilder<Object, Object> newBuilder() {
        return new StoreCacheBuilder<>();
    }

    @Nonnull
    public StoreCacheBuilder<K, V> maximumSize(long size) {
        this.maximumSize = size;
        return this;
    }


    @Nonnull
    public <K1 extends K, V1 extends V> StoreCache<K1, V1> build() {
        return new LocalStoreCache(this);
    }

}
