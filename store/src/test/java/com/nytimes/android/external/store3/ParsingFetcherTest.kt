package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingFetcher
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

class ParsingFetcherTest {

    private val fetcher: Fetcher<String, BarCode> = mock()
    private val parser: Parser<String, String> = mock()
    private val persister: Persister<String, BarCode> = mock()
    private val barCode = BarCode("key", "value")

    @Test
    fun testPersistFetcher() = runBlocking<Unit> {
        val simpleStore = StoreBuilder.barcode<String>()
                .fetcher(ParsingFetcher.from(fetcher, parser))
                .persister(persister)
                .open()

        whenever(fetcher.fetch(barCode))
                .thenReturn(RAW_DATA)

        whenever(parser.apply(RAW_DATA))
                .thenReturn(PARSED)

        whenever(persister.read(barCode))
                .thenReturn(PARSED)

        whenever(persister.write(barCode, PARSED))
                .thenReturn(true)

        val value = simpleStore.fresh(barCode)

        assertThat(value).isEqualTo(PARSED)

        verify(fetcher, times(1)).fetch(barCode)
        verify(parser, times(1)).apply(RAW_DATA)

        verify(persister, times(1)).write(barCode, PARSED)
    }

    companion object {
        internal val RAW_DATA = "Test data."
        internal val PARSED = "DATA PARSED"
    }
}
