package com.nytimes.android.external.store.base;

import android.support.annotation.NonNull;

public abstract class BaseBarcode {
    @NonNull
    private final String key;
    @NonNull
    private final String type;

    public BaseBarcode(@NonNull String type, @NonNull String key) {
        this.key = key;
        this.type = type;
    }

    public BaseBarcode() {
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
