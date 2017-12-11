package com.nytimes.android.external.store3.base;

import javax.annotation.Nonnull;

public final class ReadResultFactory {

    private ReadResultFactory() {
    }

    @Nonnull
    public static <Raw> ReadResult<Raw> createFailureResult(@Nonnull Throwable throwable) {
        if (throwable == null) {
            throw new IllegalArgumentException("throwable cannot be null.");
        }
        return new ReadResult<>(null, throwable);
    }

    @Nonnull
    public static <Raw> ReadResult<Raw> createSuccessResult(@Nonnull Raw result) {
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null.");
        }
        return new ReadResult<>(result, null);
    }
}
