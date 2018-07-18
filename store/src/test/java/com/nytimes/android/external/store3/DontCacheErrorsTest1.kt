package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder

import org.junit.Before
import org.junit.Test

import io.reactivex.Single


class DontCacheErrorsTest {

    private var shouldThrow: Boolean = false
    private lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        store = StoreBuilder.barcode<Int>()
                .fetcher { barCode ->
                    Single.fromCallable {
                        if (shouldThrow) {
                            throw RuntimeException()
                        } else {
                            0
                        }
                    }
                }
                .open()
    }

    @Test
    @Throws(InterruptedException::class)
    fun testStoreDoesntCacheErrors() {
        val barcode = BarCode("bar", "code")

        shouldThrow = true
        store.get(barcode).test()
                .assertTerminated()
                .assertError(Exception::class.java)
                .awaitTerminalEvent()

        shouldThrow = false
        store.get(barcode).test()
                .assertNoErrors()
                .awaitTerminalEvent()
    }

    @Test
    @Throws(InterruptedException::class)
    fun testStoreDoesntCacheErrorsWithResult() {
        val barcode = BarCode("bar", "code")

        shouldThrow = true
        store.getWithResult(barcode).test()
                .assertTerminated()
                .assertError(Exception::class.java)
                .awaitTerminalEvent()

        shouldThrow = false
        store.get(barcode).test()
                .assertNoErrors()
                .awaitTerminalEvent()
    }
}
