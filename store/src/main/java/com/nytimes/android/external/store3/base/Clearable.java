package com.nytimes.android.external.store3.base;


import javax.annotation.Nonnull;

/**
 * Persisters should implement Clearable if they want store.clear(key) to also clear the persister
 * @param <T> Type of key/request param in store
 */
public interface Clearable<T> {
    void clear(@Nonnull T key);
}
