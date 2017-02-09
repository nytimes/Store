package com.nytimes.android.external.store.base.impl;

import javax.annotation.Nonnull;

import rx.annotations.Beta;


/**
 * Builder where there parser is used.
 */
public final class StoreBuilder {
    private StoreBuilder() {
    }

    @Nonnull
    @Deprecated
    public static <Raw> RealStoreBuilder<Raw, Raw, BarCode> builder() {
        return new RealStoreBuilder<>();
    }

    @Nonnull
    public static <Parsed> RealStoreBuilder<Parsed, Parsed, BarCode> barcode() {
        return new RealStoreBuilder<>();
    }

    @Nonnull
    public static <Key, Parsed> RealStoreBuilder<Parsed, Parsed, Key> key() {
        return new RealStoreBuilder<>();
    }

    @Beta
    @Nonnull
    public static <Key, Raw, Parsed> RealStoreBuilder<Raw, Parsed, Key> parsedWithKey() {
        return new RealStoreBuilder<>();
    }
}
