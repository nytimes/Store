package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.base.Fetcher

/**
 * Wraps methods for fluent Store instantiation.
 */
class FluentStoreBuilder private constructor() {
    companion object {
        /**
         * Provides a fluent builder to instantiate a Store that uses BarCode objects as keys.
         * @param fetcher Fetcher for the Store.
         * @param config Optional configuration block.
         */
        fun <Parsed> barcode(
                fetcher: Fetcher<Parsed, BarCode>,
                config: (StoreParameters<Parsed, BarCode>.() -> Unit)? = null) =
                key(fetcher, config)

        /**
         * Provides a fluent builder to instantiate a Store with a custom type for keys.
         * @param fetcher Fetcher for the Store.
         * @param config Optional configuration block.
         */
        fun <Parsed, Key> key(
                fetcher: Fetcher<Parsed, Key>,
                config: (StoreParameters<Parsed,  Key>.() -> Unit)? = null) =
                StoreParameters(fetcher).apply {
                    if (config != null) {
                        this.config()
                    }
                }.let {
                    FluentRealStoreBuilder<Parsed, Parsed, Key>(
                            fetcher, it.persister, null, null, it.memoryPolicy, it.stalePolicy)
                            .open()
                }

        /**
         * Provides a fluent builder to instantiate a Store with a custom type for keys and
         * conversion between raw and parsed types.
         * @param fetcher Fetcher for the Store.
         * @param config Optional configuration block.
         */
        fun <Key, Raw, Parsed> parsedWithKey(
                fetcher: Fetcher<Raw, Key>,
                config: (ParsableStoreParameters<Raw, Parsed, Key>.() -> Unit)? = null) =
                ParsableStoreParameters<Raw, Parsed, Key>(fetcher).apply {
                    if (config != null) {
                        this.config()
                    }
                }.let {
                    FluentRealStoreBuilder(
                            fetcher, it.persister, it.parser, it.parsers, it.memoryPolicy,
                            it.stalePolicy)
                            .open()
                }
    }
}
