package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.util.KeyParser
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
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
            .parser(object : KeyParser<Int, String, String> {
                override suspend fun apply(key: Int, raw: String): String {
                    return raw + key
                }
            })
            .fetcher { NETWORK }
            .open()
    }

    @Test
    @Throws(Exception::class)
    fun testStoreWithKeyParserFuncNoPersister() = runBlocking<Unit> {
        assertThat(store.get(KEY)).isEqualTo(NETWORK + KEY)
    }

    companion object {

        private const val NETWORK = "Network"
        const val KEY = 5
    }
}
