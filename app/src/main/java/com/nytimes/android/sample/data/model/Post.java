package com.nytimes.android.sample.data.model;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Post {
    @Nullable
    public abstract Preview preview();

    public abstract String title();

    public abstract String url();

    @Nullable
    public abstract Integer height();

    @Nullable
    public abstract Integer width();

}
