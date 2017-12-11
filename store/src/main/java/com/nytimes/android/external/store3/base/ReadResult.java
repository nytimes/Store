package com.nytimes.android.external.store3.base;

import javax.annotation.Nullable;

public final class ReadResult<Raw> {
    private final Raw raw;
    private final Throwable throwable;

    ReadResult(Raw raw, Throwable throwable) {
        this.raw = raw;
        this.throwable = throwable;
    }

    @Nullable
    public Raw getResult() {
        return raw;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isSuccess() {
        return raw != null;
    }
}
