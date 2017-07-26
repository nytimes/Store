package com.nytimes.android.external.fs3;


import com.nytimes.android.external.store3.base.impl.BarCode;

import javax.annotation.Nonnull;

public class BarCodeReadAllPathResolver implements PathResolver<BarCode> {

    @Nonnull
    @Override
    public String resolve(@Nonnull BarCode barCode) {
        return barCode.getType() + "/" + barCode.getKey();
    }
}
