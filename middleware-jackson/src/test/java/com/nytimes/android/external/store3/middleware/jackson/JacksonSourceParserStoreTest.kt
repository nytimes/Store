package com.nytimes.android.external.store3.middleware.jackson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class JacksonSourceParserStoreTest {
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
    fun testDefaultJacksonSourceParser() {
        val parser = JacksonParserFactory.createSourceParser<Foo>(Foo::class.java)
        val store = StoreBuilder.parsedWithKey<BarCode, BufferedSource, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode).blockingGet()
        validateFoo(result)
        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testCustomJsonFactorySourceParser() {
        val jsonFactory = JsonFactory()

        val parser = JacksonParserFactory.createSourceParser<Foo>(jsonFactory, Foo::class.java)
        val store = StoreBuilder.parsedWithKey<BarCode, BufferedSource, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode).blockingGet()
        validateFoo(result)
        verify<Fetcher<BufferedSource, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    private fun validateFoo(foo: Foo) {
        assertNotNull(foo)
        assertEquals(foo.number.toLong(), 123)
        assertEquals(foo.string, "abc")
        assertEquals(foo.bars.size.toLong(), 2)
        assertEquals(foo.bars[0].string, "def")
        assertEquals(foo.bars[1].string, "ghi")
    }

    @Test
    fun testNullJsonFactory() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>((null as JsonFactory?)!!, Foo::class.java)
    }

    @Test
    fun testNullTypeWithValidJsonFactory() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>(JsonFactory(), null!!)
    }

    @Test
    fun testNullObjectMapper() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>((null as ObjectMapper?)!!, Foo::class.java)
    }

    @Test
    fun testNullType() {
        expectedException.expect(NullPointerException::class.java)
        JacksonParserFactory.createStringParser<Any>(null!!)
    }

    companion object {

        private val KEY = "key"
        private val sourceString = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}"
        private fun source(data: String): BufferedSource = Okio.buffer(Okio.source(ByteArrayInputStream(data.toByteArray(Charset.defaultCharset()))))
    }
}
