package com.nytimes.android.external.store3.base.impl;

import com.nytimes.android.external.cache3.Preconditions;
import com.nytimes.android.external.store3.base.Persister;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * {@link BarCode Barcode} is used as a unique
 * identifier for a particular {@link Store  Store}
 * <p/>
 * Barcode will be passed to   Fetcher
 * and {@link Persister  Persister}
 **/
@SuppressWarnings("PMD.SimplifyBooleanReturns")
public final class BarCode implements Serializable {

    private static final BarCode EMPTY_BARCODE = new BarCode("", "");

    @Nonnull
    private final String key;
    @Nonnull
    private final String type;

    public BarCode(@Nonnull String type, @Nonnull String key) {
        this.key = Preconditions.checkNotNull(key);
        this.type = Preconditions.checkNotNull(type);
    }

    @Nonnull
    public static BarCode empty() {
        return EMPTY_BARCODE;
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

    @Override
    public String toString() {
        return "BarCode{" +
                "key='" + key + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
