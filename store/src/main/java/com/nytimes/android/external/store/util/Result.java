package com.nytimes.android.external.store.util;

/**
 * Result is a container with meta data for the parsed object.
 *
 * @param <Parsed> data type after parsing
 */
public final class Result<Parsed> {

    public static final String SOURCE_CACHE = "disk";
    public static final String SOURCE_NETWORK = "network";

    private final String source;
    private final Parsed value;

    private Result(String source, Parsed value) {
        this.source = source;
        this.value = value;
    }

    /**
     * Convenient method to create a result object from {SOURCE_CACHE}.
     * @param value data type after parsing
     * @param <T> data type after parsing
     * @return a Result object with T passed
     */
    public static <T> Result<T> createFromCache(T value) {
        return new Result<>(SOURCE_CACHE, value);
    }

    /**
     * Convenient method to create a result object from {SOURCE_NETWORK}.
     * @param value data type after parsing
     * @param <T> data type after parsing
     * @return a Result object with T passed
     */
    public static <T> Result<T> createFromNetwork(T value) {
        return new Result<>(SOURCE_NETWORK, value);
    }

    public String source() {
        return source;
    }

    public Parsed value() {
        return value;
    }

    public boolean isFromNetwork() {
        return source.equals(SOURCE_NETWORK);
    }

    public boolean isFromCache() {
        return source.equals(SOURCE_CACHE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Result<?> result = (Result<?>) o;

        if (source != null && !source.equals(result.source)) {
            return false;
        }

        if (source == null && result.source != null) {
            return false;
        }

        if (value != null) {
            return value.equals(result.value);
        }

        return result.value == null;
    }

    @Override
    public int hashCode() {
        int result = 0;

        if (source != null) {
            result = source.hashCode();
        }

        result = 31 * result;
        if (value != null) {
            result += value.hashCode();
        }

        return result;
    }
}
