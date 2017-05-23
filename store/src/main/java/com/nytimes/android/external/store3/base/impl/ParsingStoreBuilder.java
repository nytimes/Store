package com.nytimes.android.external.store3.base.impl;

import javax.annotation.Nonnull;

/**
 * Builder where there parser is used.
 */
@Deprecated
public final class ParsingStoreBuilder {

    private ParsingStoreBuilder() {

    }

    @Nonnull
    public static <Raw, Parsed> RealStoreBuilder<Raw, Parsed, BarCode> builder() {
        return new RealStoreBuilder<>();
    }
}
