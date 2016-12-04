package com.nytimes.android.sample.data.model;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class Data {
    public abstract List<Children> children();
}
