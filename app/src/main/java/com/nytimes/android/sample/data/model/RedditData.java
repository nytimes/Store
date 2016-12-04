package com.nytimes.android.sample.data.model;

import org.immutables.value.Value;

@Value.Immutable
public abstract class RedditData {
    public abstract Data data();
    public abstract String kind();
}
