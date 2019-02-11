//package com.nytimes.android.sample
//
//import com.nytimes.android.external.store3.base.impl.Store
//import com.nytimes.android.external.store3.base.impl.StoreBuilder
//
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.runners.MockitoJUnitRunner
//
//import io.reactivex.Single
//import io.reactivex.observers.TestObserver
//
//
//@RunWith(MockitoJUnitRunner::class)
//class KeyParserTest {
//    private var store: Store<String, Int>? = null
//
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        store = StoreBuilder.parsedWithKey<Int, String, String>()
//                .parser({ integer, s -> s + integer })
//                .fetcher({ integer -> Single.just(NETWORK) })
//                .open()
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testStoreWithKeyParserFuncNoPersister() {
//        val testObservable = store!!.get(KEY).test().await()
//        testObservable.assertNoErrors()
//                .assertValues(NETWORK + KEY)
//                .awaitTerminalEvent()
//    }
//
//    companion object {
//
//        val NETWORK = "Network"
//        val KEY = 5
//    }
//}
