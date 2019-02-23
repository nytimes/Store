package com.nytimes.android.external.store3.middleware.moshi

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder
import kotlinx.coroutines.runBlocking
import okio.BufferedSource
import okio.Okio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class MoshiSourceParserTest {
    @Rule
    @JvmField
    var expectedException = ExpectedException.none()
    private val fetcher: Fetcher<BufferedSource, BarCode> = mock()
    private val persister: Persister<BufferedSource, BarCode> = mock()
    private val barCode = BarCode("value", KEY)

    fun setUp() = runBlocking<Unit> {
        val bufferedSource = source(sourceString)
        assertNotNull(bufferedSource)

        whenever(fetcher.fetch(barCode))
                .thenReturn(bufferedSource)

        whenever(persister.read(barCode))
                .thenReturn(null)
                .thenReturn(bufferedSource)

        whenever(persister.write(barCode, bufferedSource))
                .thenReturn(true)
    }

    @Test
    fun testSourceParser() = runBlocking<Unit> {

        val parser = MoshiParserFactory.createSourceParser<Foo>(Foo::class.java)

        val store = ParsingStoreBuilder.builder<BufferedSource, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode)

        assertEquals(result.number.toLong(), 123)
        assertEquals(result.string, "abc")
        assertEquals(result.bars.size.toLong(), 2)
        assertEquals(result.bars[0].string, "def")
        assertEquals(result.bars[1].string, "ghi")

        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)

    }

    @Test
    fun testNullMoshi() {
        expectedException.expect(NullPointerException::class.java)
        MoshiParserFactory.createSourceParser<Any>(null!!, Foo::class.java)
    }

    @Test
    fun testNullType() {
        expectedException.expect(NullPointerException::class.java)
        MoshiParserFactory.createSourceParser<Any>(null!!, Foo::class.java)
    }

    companion object {

        private val KEY = "key"
        private val sourceString = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}"

        private fun source(data: String): BufferedSource {
            return Okio.buffer(Okio.source(ByteArrayInputStream(data.toByteArray(Charset.defaultCharset()))))
        }
    }

}
