package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import java.util.concurrent.atomic.AtomicInteger

class GetRefreshingTest {
    val persister: ClearingPersister = mock()
    val networkCalls = AtomicInteger(0)
    private val store: Store<Int, BarCode> = StoreBuilder.barcode<Int>()
            .fetcher { networkCalls.incrementAndGet() }
            .persister(persister)
            .open()

//    @Test
//    fun testRefreshOnClear() = runBlocking {
//        val barcode = BarCode("type", "key")
//        `when`<Any>(persister.read(barcode))
//                .thenReturn(Maybe.empty<Int>()) //read from disk
//                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
//                .thenReturn(Maybe.empty<Int>()) //read from disk after clearing disk cache
//                .thenReturn(Maybe.just(1)) //read from disk after making additional network call
//        `when`(persister.write(barcode, 1)).thenReturn(Single.just<T>(true))
//        `when`(persister.write(barcode, 2)).thenReturn(Single.just<T>(true))
//
//
//        val refreshingObservable = store.getRefreshing(barcode).test()
//        refreshingObservable.assertValueCount(1)
//        assertThat(networkCalls.toInt()).isEqualTo(1)
//        //clearing the store should produce another network call
//        store.clear(barcode)
//        refreshingObservable.assertValueCount(2)
//        assertThat(networkCalls.toInt()).isEqualTo(2)
//
//        store.get(barcode).test().awaitTerminalEvent()
//        refreshingObservable.assertValueCount(2)
//        assertThat(networkCalls.toInt()).isEqualTo(2)
//    }
//
//    @Test
//    fun testRefreshOnClearAll() {
//        val barcode1 = BarCode("type", "key")
//        val barcode2 = BarCode("type", "key2")
//
//        `when`<Any>(persister.read(barcode1))
//                .thenReturn(Maybe.empty<Int>()) //read from disk
//                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
//                .thenReturn(Maybe.empty<Int>()) //read from disk after clearing disk cache
//                .thenReturn(Maybe.just(1)) //read from disk after making additional network call
//        `when`(persister.write(barcode1, 1)).thenReturn(Single.just<T>(true))
//        `when`(persister.write(barcode1, 2)).thenReturn(Single.just<T>(true))
//
//        `when`<Any>(persister.read(barcode2))
//                .thenReturn(Maybe.empty<Int>()) //read from disk
//                .thenReturn(Maybe.just(1)) //read from disk after fetching from network
//                .thenReturn(Maybe.empty<Int>()) //read from disk after clearing disk cache
//                .thenReturn(Maybe.just(1)) //read from disk after making additional network call
//
//        `when`(persister.write(barcode2, 1)).thenReturn(Single.just<T>(true))
//        `when`(persister.write(barcode2, 2)).thenReturn(Single.just<T>(true))
//
//        val testObservable1 = store.getRefreshing(barcode1).test()
//        val testObservable2 = store.getRefreshing(barcode2).test()
//        testObservable1.assertValueCount(1)
//        testObservable2.assertValueCount(1)
//
//        assertThat(networkCalls.toInt()).isEqualTo(2)
//
//        store.clear()
//        assertThat(networkCalls.toInt()).isEqualTo(4)
//
//
//    }
}
