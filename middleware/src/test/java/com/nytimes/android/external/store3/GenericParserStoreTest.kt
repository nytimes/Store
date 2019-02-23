package com.nytimes.android.external.store3

import com.google.common.base.Charsets.UTF_8
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.middleware.GsonParserFactory
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.io.ByteArrayInputStream

class GenericParserStoreTest {
    private val fetcher: Fetcher<BufferedSource, BarCode> = mock()
    private val persister: Persister<BufferedSource, BarCode> = mock()
    private val barCode = BarCode("value", KEY)

    @Test
    fun testSimple() = runBlocking<Unit> {
        val parser = GsonParserFactory.createSourceParser<Foo>(Gson(), Foo::class.java)

        val simpleStore = StoreBuilder.parsedWithKey<BarCode, BufferedSource, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val foo = Foo()
        foo.bar = barCode.key

        val sourceData = Gson().toJson(foo)


        val source = source(sourceData)
        whenever(fetcher.fetch(barCode))
                .thenReturn(source)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(source)

        whenever(persister.write(barCode, source))
                .thenReturn(true)

        var result = simpleStore.get(barCode)
        assertThat(result.bar).isEqualTo(KEY)
        result = simpleStore.get(barCode)
        assertThat(result.bar).isEqualTo(KEY)
        verify(fetcher, times(1)).fetch(barCode)
    }

    private class Foo internal constructor() {
        internal var bar: String? = null
    }

    companion object {
        val KEY = "key"

        private fun source(data: String): BufferedSource {
            return Okio.buffer(Okio.source(ByteArrayInputStream(data.toByteArray(UTF_8))))
        }
    }
}
