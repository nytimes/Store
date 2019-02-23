package com.nytimes.android.external.store.util

/**
 * Result is a container with meta data for the parsed object.
 *
 * @param <Parsed> data type after parsing
</Parsed> */
data class Result<Parsed>(val source: Source, val value: Parsed?) {

    val isFromNetwork: Boolean
        get() = source == Source.NETWORK

    val isFromCache: Boolean
        get() = source == Source.CACHE

    enum class Source {
        CACHE, NETWORK
    }

    companion object {

        /**
         * Convenient method to create a result object from {SOURCE_CACHE}.
         * @param value data type after parsing
         * @param <T> data type after parsing
         * @return a Result object with T passed
        </T> */
        fun <T> createFromCache(value: T): Result<T> {
            return Result(Source.CACHE, value)
        }

        /**
         * Convenient method to create a result object from {SOURCE_NETWORK}.
         * @param value data type after parsing
         * @param <T> data type after parsing
         * @return a Result object with T passed
        </T> */
        fun <T> createFromNetwork(value: T): Result<T> {
            return Result(Source.NETWORK, value)
        }
    }
}
