package com.nytimes.android.external.store3.middleware.moshi

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.ParsingStoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
import okio.BufferedSource
import okio.Okio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class MoshiSourceParserTest {
    @Rule
    @JvmField
    var expectedException = ExpectedException.none()
    @Mock
    lateinit var fetcher: Fetcher<BufferedSource, BarCode>
    @Mock
    lateinit var persister: Persister<BufferedSource, BarCode>
    private val barCode = BarCode("value", KEY)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        val bufferedSource = source(sourceString)
        assertNotNull(bufferedSource)

        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(bufferedSource))

        `when`(persister.read(barCode))
                .thenReturn(Maybe.empty())
                .thenReturn(Maybe.just(bufferedSource))

        `when`(persister.write(barCode, bufferedSource))
                .thenReturn(Single.just(true))
    }

    @Test
    @Throws(Exception::class)
    fun testSourceParser() {

        val parser = MoshiParserFactory.createSourceParser<Foo>(Foo::class.java)

        val store = ParsingStoreBuilder.builder<BufferedSource, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode).blockingGet()

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
