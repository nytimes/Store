//package com.nytimes.android.sample
//
//import com.nytimes.android.external.store3.base.Fetcher
//import com.nytimes.android.external.store3.base.Persister
//import com.nytimes.android.external.store3.base.impl.BarCode
//import com.nytimes.android.external.store3.base.impl.Store
//import com.nytimes.android.external.store3.base.impl.StoreBuilder
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.runners.MockitoJUnitRunner
//import java.util.concurrent.atomic.AtomicInteger
//
//@RunWith(MockitoJUnitRunner::class)
//class StreamTest {
//
//  lateinit var counter: AtomicInteger
//  lateinit var fetcher: Fetcher<String, BarCode>
//  lateinit var persister: Persister<String, BarCode>
//  private val barCode = BarCode("key", "value")
//  lateinit var store: Store<String, BarCode>
//  @Before
//  fun setUp() {
//    fetcher = object : Fetcher<String, BarCode> {
//      override suspend fun fetch(key: BarCode) = counter.incrementAndGet().toString()
//    }
//
//
//    persister = object : Persister<String, BarCode> {
//      override suspend fun read(key: BarCode): String? {
//        when {
//          counter.get() == 0 -> return null
//          counter.get() == 1 -> return SmokeTests.DISK
//          else -> return "WRONG VALUE"
//        }
//      }
//
//      override suspend fun write(
//        key: BarCode,
//        raw: String
//      ): Boolean {
//        return true
//      }
//    }
//
//    val store = StoreBuilder.barcode<String>()
//        .persister(persister)
//        .fetcher(fetcher)
//        .open()
//  }
//
//  @Test
//  fun testStream() {
//    val streamObservable = store.stream()
//        .test()
//    streamObservable.assertValueCount(0)
//    store!!.get(barCode)
//        .subscribe()
//    streamObservable.assertValueCount(1)
//  }
//
//  @Test
//  fun testStreamEmitsOnlyFreshData() {
//    store!!.get(barCode)
//        .subscribe()
//    val streamObservable = store!!.stream()
//        .test()
//    streamObservable.assertValueCount(0)
//  }
//
//  companion object {
//
//    private val TEST_ITEM = "test"
//  }
//}
