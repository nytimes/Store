package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class KeyParserTest {
    lateinit var store: Store<String, Int>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        store = StoreBuilder.parsedWithKey<Int, String, String>()
                .parser { integer, s -> s + integer }
                .fetcher { integer -> Single.just(NETWORK) }
                .open()
    }

    @Test
    @Throws(Exception::class)
    fun testStoreWithKeyParserFuncNoPersister() {
        val testObservable = store.get(KEY).test().await()
        testObservable.assertNoErrors()
                .assertValues(NETWORK + KEY)
                .awaitTerminalEvent()
    }

    companion object {

        private const val NETWORK = "Network"
        val KEY = 5
    }
}
