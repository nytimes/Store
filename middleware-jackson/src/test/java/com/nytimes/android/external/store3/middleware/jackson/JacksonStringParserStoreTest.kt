package com.nytimes.android.external.store3.middleware.jackson

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.Persister
import com.nytimes.android.external.store3.base.impl.BarCode
import com.nytimes.android.external.store3.base.impl.StoreBuilder
import io.reactivex.Maybe
import io.reactivex.Single
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

class JacksonStringParserStoreTest {
    @Rule
    @JvmField
    var expectedException = ExpectedException.none()
    @Mock
    lateinit var fetcher: Fetcher<String, BarCode>
    @Mock
    lateinit var persister: Persister<String, BarCode>
    private val barCode = BarCode("value", KEY)

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        `when`(fetcher.fetch(barCode))
                .thenReturn(Single.just(source))

        `when`(persister.read(barCode))
                .thenReturn(Maybe.empty())
                .thenReturn(Maybe.just(source))

        `when`(persister.write(barCode, source))
                .thenReturn(Single.just(true))
    }

    @Test
    fun testDefaultJacksonStringParser() {
        val store = StoreBuilder.parsedWithKey<BarCode, String, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(JacksonParserFactory.createStringParser(Foo::class.java))
                .open()

        val result = store.get(barCode).blockingGet()
        validateFoo(result)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
    }

    @Test
    fun testCustomJsonFactoryStringParser() {
        val jsonFactory = JsonFactory()

        val parser = JacksonParserFactory.createStringParser<Foo>(jsonFactory, Foo::class.java)

        val store = StoreBuilder.parsedWithKey<BarCode, String, Foo>()
                .persister(persister)
                .fetcher(fetcher)
                .parser(parser)
                .open()

        val result = store.get(barCode).blockingGet()
        validateFoo(result)
        verify<Fetcher<String, BarCode>>(fetcher, times(1)).fetch(barCode)
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
        private val source = "{\"number\":123,\"string\":\"abc\",\"bars\":[{\"string\":\"def\"},{\"string\":\"ghi\"}]}"
    }
}
