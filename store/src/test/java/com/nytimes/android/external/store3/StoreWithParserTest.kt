package com.nytimes.android.external.store3

import com.nhaarman.mockitokotlin2.mock
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.*
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

        `when`<Any>(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK))

        `when`<Any>(persister.read(barCode))
                .thenReturn(Maybe.empty<String>())
                .thenReturn(Maybe.just(DISK))

        `when`<Any>(persister.write(barCode, NETWORK))
                .thenReturn(Single.just(true))

        `when`<Any>(parser.apply(DISK)).thenReturn(barCode.key)

        var value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

//    @Test
//    fun testSimpleWithResult() = runBlocking<Unit> {
//        val simpleStore = ParsingStoreBuilder.builder<String, String>()
//                .persister(persister)
//                .fetcher(fetcher)
//                .parser(parser)
//                .open()
//
//        `when`<Any>(fetcher.fetch(barCode))
//                .thenReturn(Single.just(NETWORK))
//
//        `when`<Any>(persister.read(barCode))
//                .thenReturn(Maybe.empty<String>())
//                .thenReturn(Maybe.just(DISK))
//
//        `when`<Any>(persister.write(barCode, NETWORK))
//                .thenReturn(Single.just(true))
//
//        `when`<Any>(parser.apply(DISK)).thenReturn(barCode.key)
//
//        var result = simpleStore.getWithResult(barCode)
//        assertThat(result.source()).isEqualTo(Result.Source.NETWORK)
//        assertThat(result.value()).isEqualTo(barCode.key)
//
//        result = simpleStore.getWithResult(barCode)
//        assertThat(result.source()).isEqualTo(Result.Source.CACHE)
//        assertThat(result.value()).isEqualTo(barCode.key)
//        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
//    }

    @Test
    fun testSubclass() = runBlocking<Unit> {
        MockitoAnnotations.initMocks(this)

        val simpleStore = SampleParsingStore(fetcher, persister, parser)

        `when`<Any>(fetcher.fetch(barCode))
                .thenReturn(Single.just(NETWORK))

        `when`<Any>(persister.read(barCode))
                .thenReturn(Maybe.empty<String>())
                .thenReturn(Maybe.just(DISK))

        `when`<Any>(persister.write(barCode, NETWORK))
                .thenReturn(Single.just(true))

        `when`<Any>(parser.apply(DISK)).thenReturn(barCode.key)

        var value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        value = simpleStore.get(barCode)
        assertThat(value).isEqualTo(barCode.key)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

//    @Test
//    fun testSubclassWithResult() = runBlocking<Unit> {
//        MockitoAnnotations.initMocks(this)
//
//        val simpleStore = SampleParsingStore(fetcher, persister, parser)
//
//        `when`<Any>(fetcher.fetch(barCode))
//                .thenReturn(Single.just(NETWORK))
//
//        `when`<Any>(persister.read(barCode))
//                .thenReturn(Maybe.empty<String>())
//                .thenReturn(Maybe.just(DISK))
//
//        `when`<Any>(persister.write(barCode, NETWORK))
//                .thenReturn(Single.just(true))
//
//        `when`<Any>(parser.apply(DISK)).thenReturn(barCode.key)
//
//        var result = simpleStore.getWithResult(barCode)
//        assertThat(result.source()).isEqualTo(Result.Source.NETWORK)
//        assertThat(result.value()).isEqualTo(barCode.key)
//
//        result = simpleStore.getWithResult(barCode)
//        assertThat(result.source()).isEqualTo(Result.Source.CACHE)
//        assertThat(result.value()).isEqualTo(barCode.key)
//        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
//    }

    companion object {

        private val DISK = "persister"
        private val NETWORK = "fresh"
    }
}
