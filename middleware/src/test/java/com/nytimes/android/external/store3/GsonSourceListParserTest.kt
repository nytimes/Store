package com.nytimes.android.external.store3

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Parser
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.middleware.GsonParserFactory

import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.io.ByteArrayInputStream
import java.util.Arrays

import io.reactivex.Maybe
import io.reactivex.Single
import okio.BufferedSource
import okio.Okio

import com.google.common.base.Charsets.UTF_8
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class GsonSourceListParserTest {
    @Mock
    lateinit var fetcher: Fetcher<BufferedSource, BarCode>
    @Mock
    lateinit var persister: Persister<BufferedSource, BarCode>
    private val barCode = BarCode("value", KEY)

    @Test
    fun testSimple() {
        MockitoAnnotations.initMocks(this)

        val parser = GsonParserFactory.createSourceParser<List<Foo>>(Gson(), object : TypeToken<List<Foo>>() {

        }.type)


        val simpleStore = StoreBuilder.parsedWithKey<BarCode, BufferedSource, List<Foo>>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val foo = Foo("a")
        val foo2 = Foo("b")
        val foo3 = Foo("c")
        val data = Arrays.asList(foo, foo2, foo3)

        val sourceData = Gson().toJson(data)


        val source = source(sourceData)
        val value = Single.just(source)
        `when`(fetcher.fetch(barCode))
                .thenReturn(value)

        `when`(persister.read(barCode))
                .thenReturn(Maybe.empty())
                .thenReturn(value.toMaybe())

        `when`(persister.write(barCode, source))
                .thenReturn(Single.just(true))

        val result = simpleStore.get(barCode).blockingGet()
        assertThat(result[0].value).isEqualTo("a")
        assertThat(result[1].value).isEqualTo("b")
        assertThat(result[2].value).isEqualTo("c")

        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    private class Foo internal constructor(internal var value: String)

    companion object {
        val KEY = "key"

        private fun source(data: String): BufferedSource {
            return Okio.buffer(Okio.source(ByteArrayInputStream(data.toByteArray(UTF_8))))
        }
    }
}
