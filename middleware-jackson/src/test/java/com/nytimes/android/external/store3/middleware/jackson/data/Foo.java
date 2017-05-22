package com.nytimes.android.external.store3.middleware.jackson.data;

import java.util.List;

public class Foo {
    public int number;
    public String string;
    public List<Bar> bars;

    public Foo() {
    }

    public Foo(int number, String string, List<Bar> bars) {
        this.number = number;
        this.string = string;
        this.bars = bars;
    }
}
