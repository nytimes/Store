package com.nytimes.android.external.store3

import com.nytimes.android.external.store.util.Result
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class StoreWithParserTest {
    @Mock
    internal var fetcher: Fetcher<String, BarCode>? = null
    @Mock
    internal var persister: Persister<String, BarCode>? = null
    @Mock
    internal var parser: Parser<String, String>? = null

    private val barCode = BarCode("key", "value")

    @Test
    @Throws(Exception::class)
    fun testSimple() {
        MockitoAnnotations.initMocks(this)


        val simpleStore = ParsingStoreBuilder.builder<String, String>()
                .persister(persister!!)
                .fetcher(fetcher!!)
                .parser(parser!!)
                .open()

        `when`<Any>(fetcher!!.fetch(barCode))
                .thenReturn(Single.just(NETWORK))

        `when`<Any>(persister!!.read(barCode))
                .thenReturn(Maybe.empty<String>())
                .thenReturn(Maybe.just(DISK))

        `when`<Any>(persister!!.write(barCode, NETWORK))
                .thenReturn(Single.just(true))

        `when`<Any>(parser!!.apply(DISK)).thenReturn(barCode.key)

        var value = simpleStore.get(barCode).blockingGet()
        assertThat(value).isEqualTo(barCode.key)
        value = simpleStore.get(barCode).blockingGet()
        assertThat(value).isEqualTo(barCode.key)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    @Test
    @Throws(Exception::class)
    fun testSimpleWithResult() {
        MockitoAnnotations.initMocks(this)


        val simpleStore = ParsingStoreBuilder.builder<String, String>()
                .persister(persister!!)
                .fetcher(fetcher!!)
                .parser(parser!!)
                .open()

        `when`<Any>(fetcher!!.fetch(barCode))
                .thenReturn(Single.just(NETWORK))

        `when`<Any>(persister!!.read(barCode))
                .thenReturn(Maybe.empty<String>())
                .thenReturn(Maybe.just(DISK))

        `when`<Any>(persister!!.write(barCode, NETWORK))
                .thenReturn(Single.just(true))

        `when`<Any>(parser!!.apply(DISK)).thenReturn(barCode.key)

        var result = simpleStore.getWithResult(barCode).blockingGet()
        assertThat<Source>(result.source()).isEqualTo(Result.Source.NETWORK)
        assertThat(result.value()).isEqualTo(barCode.key)

        result = simpleStore.getWithResult(barCode).blockingGet()
        assertThat<Source>(result.source()).isEqualTo(Result.Source.CACHE)
        assertThat(result.value()).isEqualTo(barCode.key)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    @Test
    @Throws(Exception::class)
    fun testSubclass() {
        MockitoAnnotations.initMocks(this)

        val simpleStore = SampleParsingStore(fetcher!!, persister!!, parser!!)

        `when`<Any>(fetcher!!.fetch(barCode))
                .thenReturn(Single.just(NETWORK))

        `when`<Any>(persister!!.read(barCode))
                .thenReturn(Maybe.empty<String>())
                .thenReturn(Maybe.just(DISK))

        `when`<Any>(persister!!.write(barCode, NETWORK))
                .thenReturn(Single.just(true))

        `when`<Any>(parser!!.apply(DISK)).thenReturn(barCode.key)

        var value = simpleStore.get(barCode).blockingGet()
        assertThat(value).isEqualTo(barCode.key)
        value = simpleStore.get(barCode).blockingGet()
        assertThat(value).isEqualTo(barCode.key)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    @Test
    @Throws(Exception::class)
    fun testSubclassWithResult() {
        MockitoAnnotations.initMocks(this)

        val simpleStore = SampleParsingStore(fetcher!!, persister!!, parser!!)

        `when`<Any>(fetcher!!.fetch(barCode))
                .thenReturn(Single.just(NETWORK))

        `when`<Any>(persister!!.read(barCode))
                .thenReturn(Maybe.empty<String>())
                .thenReturn(Maybe.just(DISK))

        `when`<Any>(persister!!.write(barCode, NETWORK))
                .thenReturn(Single.just(true))

        `when`<Any>(parser!!.apply(DISK)).thenReturn(barCode.key)

        var result = simpleStore.getWithResult(barCode).blockingGet()
        assertThat<Source>(result.source()).isEqualTo(Result.Source.NETWORK)
        assertThat(result.value()).isEqualTo(barCode.key)

        result = simpleStore.getWithResult(barCode).blockingGet()
        assertThat<Source>(result.source()).isEqualTo(Result.Source.CACHE)
        assertThat(result.value()).isEqualTo(barCode.key)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    companion object {

        private val DISK = "persister"
        private val NETWORK = "fresh"
    }
}
