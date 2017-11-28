package com.nytimes.android.external.fs3;

import javax.annotation.Nonnull;

public final class ReadResultBufferedSourceFactory {

    private ReadResultBufferedSourceFactory() {
    }

    @Nonnull
    public static ReadResultBufferedSource createFailureResult(@Nonnull Throwable throwable) {
        if (throwable == null) {
            throw new IllegalArgumentException("throwable cannot be null.");
        }
        return new ReadResultBufferedSource(throwable);
    }
}
