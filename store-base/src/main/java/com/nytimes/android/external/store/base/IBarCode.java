package com.nytimes.android.external.store.base;

import android.support.annotation.NonNull;

public abstract class IBarCode {
    @NonNull
    private final String key;
    @NonNull
    private final String type;

    public IBarCode(@NonNull String type, @NonNull String key) {
        this.key = key;
        this.type = type;
    }

    public IBarCode() {
        key="";
        type="";
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public String getType() {
        return type;
    }


}
