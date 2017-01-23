package com.nytimes.android.external.store.base.impl;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * {@link com.nytimes.android.external.store.base.impl.BarCode Barcode} is used as a unique
 * identifier for a particular {@link com.nytimes.android.external.store.base.Store  Store}
 * <p/>
 * Barcode will be passed to {@link com.nytimes.android.external.store.base.Fetcher  Fetcher}
 * and {@link com.nytimes.android.external.store.base.Persister  Persister}
 **/

public final class BarCode implements Serializable {
    @NonNull
    private final String key;
    @NonNull
    private final String type;

    public BarCode(@NonNull String type, @NonNull String key) {
        this.key = key;
        this.type = type;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public String getType() {
        return type;
    }

    @NonNull
    public static BarCode empty() {
        return new BarCode("", "");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BarCode)) {
            return false;
        }
        BarCode barCode = (BarCode) object;

        if (!key.equals(barCode.key)) {
            return false;
        }
        if (!type.equals(barCode.type)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
