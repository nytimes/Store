package com.nytimes.android.external.fs3;

import com.nytimes.android.external.store3.base.impl.BarCode;

import javax.annotation.Nonnull;

class BarCodePathResolver implements PathResolver<BarCode> {
    @Nonnull
    @Override
    public String resolve(@Nonnull BarCode key) {
        return key.toString();
    }
}
