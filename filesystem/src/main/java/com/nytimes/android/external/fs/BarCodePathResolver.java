package com.nytimes.android.external.fs;

import com.nytimes.android.external.store.base.impl.BarCode;

class BarCodePathResolver implements PathResolver<BarCode> {
    @Override
    public String resolve(BarCode key) {
        return key.toString();
    }
}
