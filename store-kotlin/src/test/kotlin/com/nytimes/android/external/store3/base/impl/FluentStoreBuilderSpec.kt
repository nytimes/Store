package com.nytimes.android.external.store3.base.impl

import com.nytimes.android.external.store3.base.Fetcher
import org.apache.commons.lang3.builder.EqualsBuilder
import org.junit.Test
import org.mockito.Mockito

/**
 * Spec for FluentStoreBuilder.
 */
class FluentStoreBuilderSpec {
    @Test
    fun equivalentBarcode() {
        @Suppress("UNCHECKED_CAST")
        val fetcher = Mockito.mock(Fetcher::class.java) as Fetcher<Any, BarCode>
        val javaResult = StoreBuilder.barcode<Any>()
                .fetcher(fetcher)
                .open()
        val kotlinResult = FluentStoreBuilder.barcode(fetcher)
        assertEquivalent(javaResult, kotlinResult)
    }

    @Test
    fun equivalentKey() {
        @Suppress("UNCHECKED_CAST")
        val fetcher = Mockito.mock(Fetcher::class.java) as Fetcher<Any, BarCode>
        val javaResult = StoreBuilder.key<BarCode, Any>()
                .fetcher(fetcher)
                .open()
        val kotlinResult = FluentStoreBuilder.key(fetcher)
        assertEquivalent(javaResult, kotlinResult)
    }

    @Test
    fun equivalentParsedWithKey() {
        @Suppress("UNCHECKED_CAST")
        val fetcher = Mockito.mock(Fetcher::class.java) as Fetcher<Any, BarCode>
        val javaResult = StoreBuilder.parsedWithKey<BarCode, Any, Any>()
                .fetcher(fetcher)
                .open()
        val kotlinResult = FluentStoreBuilder.parsedWithKey<BarCode, Any, Any>(fetcher)
        assertEquivalent(javaResult, kotlinResult)
    }

    private fun <T, V> assertEquivalent(expected: Store<T, V>, actual: Store<T, V>) =
            EqualsBuilder.reflectionEquals(expected, actual)
}
