package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder

import org.junit.Before
import org.junit.Test

import io.reactivex.Single

class NoNetworkTest {
    private lateinit var store: Store<Any, BarCode>

    @Before
    fun setUp() {
        store = StoreBuilder.barcode<Any>()
                .fetcher { barcode -> Single.error(EXCEPTION) }
                .open()
    }

    @Test
    @Throws(Exception::class)
    fun testNoNetwork() {
        store.get(BarCode("test", "test"))
                .test()
                .assertError(EXCEPTION)
    }

    @Test
    @Throws(Exception::class)
    fun testNoNetworkWithResult() {
        store.getWithResult(BarCode("test", "test"))
                .test()
                .assertError(EXCEPTION)
    }

    companion object {
        private val EXCEPTION = RuntimeException()
    }
}
