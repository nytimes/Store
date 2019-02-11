package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class DontCacheErrorsTest {

    private var shouldThrow: Boolean = false
    private lateinit var store: Store<Int, BarCode>

    @Before
    fun setUp() {
        store = StoreBuilder.barcode<Int>()
                .fetcher {
                    if (shouldThrow) {
                        throw RuntimeException()
                    } else {
                        0
                    }
                }
                .open()
    }

    @Test
    fun testStoreDoesntCacheErrors() = runBlocking<Unit> {
        val barcode = BarCode("bar", "code")

        shouldThrow = true

        try {
            store.get(barcode)
            fail()
        } catch (e: Exception) {
        }

        shouldThrow = false
        store.get(barcode)
    }

//    TODO storeWithResult test
//    @Test
//    fun testStoreDoesntCacheErrorsWithResult() = runBlocking<Unit> {
//        val barcode = BarCode("bar", "code")
//
//        shouldThrow = true
//        store.getWithResult(barcode).test()
//                .assertTerminated()
//                .assertError(Exception::class.java)
//                .awaitTerminalEvent()
//
//        shouldThrow = false
//        store.get(barcode).test()
//                .assertNoErrors()
//                .awaitTerminalEvent()
//    }
}
