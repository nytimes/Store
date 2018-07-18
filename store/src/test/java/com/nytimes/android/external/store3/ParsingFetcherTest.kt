package com.nytimes.android.external.store3

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingFetcher
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ParsingFetcherTest {

    @Mock
    internal lateinit var fetcher: Fetcher<String, BarCode>
    @Mock
    internal lateinit var parser: Parser<String, String>
    @Mock
    internal lateinit var persister: Persister<String, BarCode>
    private val barCode = BarCode("key", "value")

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testPersistFetcher() {

        val simpleStore = StoreBuilder.barcode<String>()
                .fetcher(ParsingFetcher.from(fetcher, parser))
                .persister(persister)
                .open()

        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(RAW_DATA))

        `when`(parser.apply(RAW_DATA))
                .thenReturn(PARSED)

        `when`(persister.read(barCode))
                .thenReturn(Maybe.just(PARSED))

        `when`(persister.write(barCode, PARSED))
                .thenReturn(Single.just(true))

        val value = simpleStore.fetch(barCode).blockingGet()

        assertThat(value).isEqualTo(PARSED)

        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
        verify<Parser<String, String>>(parser, times(1)).apply(RAW_DATA)
        verify<Persister<String, BarCode>>(persister, times(1)).write(barCode, PARSED)
    }

    companion object {
        private const val RAW_DATA = "Test data."
        private const val PARSED = "DATA PARSED"
    }
}
