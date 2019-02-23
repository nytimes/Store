package com.nytimes.android.external.store3.base.impl


/**
 * Builder where there parser is used.
 */
object StoreBuilder {

    @Deprecated("")
    fun <Raw> builder(): RealStoreBuilder<Raw, Raw, BarCode> {
        return RealStoreBuilder()
    }

    fun <Parsed> barcode(): RealStoreBuilder<Parsed, Parsed, BarCode> {
        return RealStoreBuilder()
    }

    fun <Key, Parsed> key(): RealStoreBuilder<Parsed, Parsed, Key> {
        return RealStoreBuilder()
    }

    fun <Key, Raw, Parsed> parsedWithKey(): RealStoreBuilder<Raw, Parsed, Key> {
        return RealStoreBuilder()
    }
}
