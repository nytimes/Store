package com.nytimes.android.external.store.middleware.moshi.data;

import java.util.List;

public class Foo {
    public int number;
    public String string;
    public List<Bar> bars;

    public Foo(int number, String string, List<Bar> bars) {
        this.number = number;
        this.string = string;
        this.bars = bars;
    }
}
