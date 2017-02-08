package com.nytimes.android.external.store.base;

/**
 * Created by 206847 on 2/7/17.
 */

public interface Clearable<T> {
    void clear(T key);
}
