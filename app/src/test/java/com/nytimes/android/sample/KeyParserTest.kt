package com.nytimes.android.sample

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
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
        .parser(object : Parser<String, String> {
          override suspend fun apply(raw: String): String = raw + KEY
        })
        .fetcher(object : Fetcher<String, Int> {
          override suspend fun fetch(key: Int): String = NETWORK
        })
        .open()
  }

  @Test
  fun testStoreWithKeyParserFuncNoPersister() {
    runBlocking {
      assertThat(store.get(KEY)).isEqualTo(NETWORK + KEY)
    }
  }

  companion object {

    val NETWORK = "Network"
    val KEY = 5
  }
}
