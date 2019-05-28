package com.nytimes.android.external.store3

import com.google.gson.Gson
import com.nytimes.android.external.store3.middleware.GsonParserFactory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class GsonParserFactoryTest {

    @Rule
    @JvmField
    var expectedException = ExpectedException.none()

    private val gson = Gson()

    @Test
    fun shouldCreateParsersProperly() {
        GsonParserFactory.createReaderParser<Any>(gson)
        GsonParserFactory.createSourceParser<Any>(gson)
        GsonParserFactory.createStringParser<Any>(gson)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingReaderWithNullGson() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createReaderParser<Any>(null!!)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingSourceWithNullGson() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createSourceParser<Any>(null!!)
    }

    @Test
    fun shouldThrowExceptionWhenCreatingStringWithNullGson() {
        expectedException.expect(NullPointerException::class.java)
        GsonParserFactory.createStringParser<Any>(null!!)
    }
}
