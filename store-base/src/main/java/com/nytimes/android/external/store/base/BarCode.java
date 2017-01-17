package com.nytimes.android.external.store.base;


import org.jetbrains.annotations.NotNull;

public class BarCode {
    @NotNull
    private final String key;
    @NotNull
    private final String type;

    public BarCode(@NotNull String type, @NotNull String key) {
        this.key = key;
        this.type = type;
    }

    public BarCode() {
        key = "";
        type = "";
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public static com.nytimes.android.external.store.base.BarCode empty() {
        return new BarCode("", "");
    }

}
