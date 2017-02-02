package com.nytimes.android.external.store.base.impl;

import com.nytimes.android.external.store.base.beta.Store;


import java.io.Serializable;
import javax.annotation.Nonnull;

/**
 * {@link com.nytimes.android.external.store.base.impl.BarCode Barcode} is used as a unique
 * identifier for a particular {@link Store  Store}
 * <p/>
 * Barcode will be passed to   Fetcher
 * and {@link com.nytimes.android.external.store.base.Persister  Persister}
 **/
@Deprecated
public final class BarCode implements Serializable {
    @Nonnull
    private final String key;
    @Nonnull
    private final String type;

    public BarCode(@Nonnull String type, @Nonnull String key) {
        this.key = key;
        this.type = type;
    }

    @Nonnull
    public static BarCode empty() {
        return new BarCode("", "");
    }

    @Nonnull
    public String getKey() {
        return key;
    }

    @Nonnull
    public String getType() {
        return type;
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
