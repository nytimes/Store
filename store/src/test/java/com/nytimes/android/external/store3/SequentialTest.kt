package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SequentialTest {

    var networkCalls = 0
    private var store = StoreBuilder.barcode<Int>()
            .fetcher { networkCalls++ }
            .open()

    @Test
    fun sequentially() = runBlocking<Unit> {
        val b = BarCode("one", "two")
        store.get(b)
        store.get(b)

        assertThat(networkCalls).isEqualTo(1)
    }

//    @Test
//    fun sequentiallyWithResult() = runBlocking<Unit> {
//        val b = BarCode("one", "two")
//        store.getWithResult(b)
//        store.getWithResult(b)
//
//        assertThat(networkCalls).isEqualTo(1)
//    }

    @Test
    fun parallel() = runBlocking<Unit> {
        val b = BarCode("one", "two")
        val deferred = async { store.get(b) }
        store.get(b)
        deferred.await()

        assertThat(networkCalls).isEqualTo(1)
    }

//    @Test
//    fun parallelWithResult() = runBlocking<Unit> {
//        val b = BarCode("one", "two")
//        val first = store.getWithResult(b)
//        val second = store.getWithResult(b)
//
//        assertThat(networkCalls).isEqualTo(1)
//    }
}
