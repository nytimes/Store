package com.nytimes.android.external.fs3

import com.google.common.base.Charsets.UTF_8
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import com.nytimes.android.external.store3.middleware.GsonSourceParser
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.io.ByteArrayInputStream

class SourceFilerReaderWriterStoreTest {
    private val fetcher: Fetcher<BufferedSource, BarCode> = mock()
    private val fileReader: SourceFileReader = mock()
    private val fileWriter: SourceFileWriter = mock()
    private val barCode = BarCode("value", KEY)

    @Test
    fun fetcherOnlyCalledOnce() = runBlocking<Unit> {
        val parser = GsonSourceParser<Foo>(Gson(), Foo::class.java)
        val simpleStore = StoreBuilder.parsedWithKey<BarCode, BufferedSource, Foo>()
                .persister(fileReader, fileWriter)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val foo = Foo()
        foo.bar = barCode.key

        val sourceData = Gson().toJson(foo)

        val source = source(sourceData)
        whenever(fetcher.fetch(barCode))
                .thenReturn(source)

        whenever(fileReader.read(barCode))
                .thenReturn(null)
                .thenReturn(source)

        whenever(fileWriter.write(barCode, source))
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
        private const val KEY = "key"

        private fun source(data: String): BufferedSource = Okio.buffer(Okio.source(ByteArrayInputStream(data.toByteArray(UTF_8))))
    }

}
