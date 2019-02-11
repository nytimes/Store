package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Before
import org.junit.Test

class NoNetworkTest {
    private lateinit var store: Store<Any, BarCode>

    @Before
    fun setUp() {
        store = StoreBuilder.barcode<Any>()
                .fetcher { throw EXCEPTION }
                .open()
    }

    @Test
    fun testNoNetwork() = runBlocking<Unit> {
        try {
            store.get(BarCode("test", "test"))
            fail("Exception not thrown")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(EXCEPTION)
        }
    }

// TODO getWithResult test
//    @Test
//    fun testNoNetworkWithResult() = runBlocking<Unit> {
//        store.getWithResult(BarCode("test", "test"))
//                .test()
//                .assertError(EXCEPTION)
//    }

    companion object {
        private val EXCEPTION = RuntimeException()
    }
}
