package com.nytimes.android.sample.data.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Image {
    public abstract String url();
    public abstract int height();
    public abstract int width();
}
