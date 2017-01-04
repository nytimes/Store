package com.nytimes.android.sample.data.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Images {
    public abstract Image source();
}
