package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.annotations.Experimental
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.util.KeyParser

/**
 * A parameter box for Store instantiation, used for Stores that do not make use of parsing.
 * @param fetcher The fetcher for the Store.
 */
@Experimental
open class StoreParameters<Raw, Key> internal constructor(private val fetcher: Fetcher<Raw, Key>) {
    var persister: Persister<Raw, Key>? = null
    var memoryPolicy: MemoryPolicy? = null
    var stalePolicy: StalePolicy = StalePolicy.UNSPECIFIED
}

/**
 * A parameter box for Store instantiation, used for Stores that can have parsing.
 * @param fetcher The fetcher for the Store.
 */
@Experimental
class ParsableStoreParameters<Raw, Parsed, Key> internal constructor(fetcher: Fetcher<Raw, Key>)
    : StoreParameters<Raw, Key>(fetcher) {
    var parser: KeyParser<Key, Raw, Parsed>? = null
        set(value) {
            field = value
            if (value != null) {
                parsers = null
            }
        }
    var parsers: List<Parser<Raw, Parsed>>? = null
        set(value) {
            field = value
            if (value != null) {
                parser = null
            }
        }
}
