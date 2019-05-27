package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class StoreWithParserTest {
    val fetcher: Fetcher<String, BarCode> = mock()
    val persister: Persister<String, BarCode> = mock()
    val parser: Parser<String, String> = mock()

    private val barCode = BarCode("key", "value")

    @Test
    fun testSimple() = runBlocking<Unit> {
        val simpleStore = ParsingStoreBuilder.builder<String, String>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        whenever(fetcher.fetch(barCode))
                .thenReturn(NETWORK)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(DISK)

        whenever(persister.write(barCode, NETWORK))
                .thenReturn(true)

        whenever(parser.apply(DISK)).thenReturn(barCode.key)

        var value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        verify(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testSubclass() = runBlocking<Unit> {
        MockitoAnnotations.initMocks(this)

        val simpleStore = SampleParsingStore(fetcher, persister, parser)

        whenever(fetcher.fetch(barCode))
                .thenReturn(NETWORK)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(DISK)

        whenever(persister.write(barCode, NETWORK))
                .thenReturn(true)

        whenever(parser.apply(DISK)).thenReturn(barCode.key)

        var value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        verify(fetcher, times(1)).fetch(barCode)
    }

    companion object {
        private const val DISK = "persister"
        private const val NETWORK = "fresh"
    }
}
