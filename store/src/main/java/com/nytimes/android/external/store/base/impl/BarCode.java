package com.nytimes.android.external.store.base.impl;

import android.support.annotation.NonNull;

import com.nytimes.android.external.store.base.BaseBarcode;

import java.io.Serializable;

/**
 * {@link BarCode Barcode} is used as a unique
 * identifier for a particular {@link com.nytimes.android.external.store.base.Store  Store}
 * <p/>
 * Barcode will be passed to {@link com.nytimes.android.external.store.base.Fetcher  Fetcher}
 * and {@link com.nytimes.android.external.store.base.Persister  Persister}
 **/

public final class BarCode extends BaseBarcode implements Serializable {

    public BarCode(@NonNull String type, @NonNull String key) {
        super(type, key);
    }

    @NonNull
    public static BaseBarcode empty() {
        return new BarCode("", "");
    }
}
