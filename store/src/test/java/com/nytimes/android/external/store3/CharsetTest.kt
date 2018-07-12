package com.nytimes.android.external.store3

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.mockito.MockitoAnnotations

import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException

import org.assertj.core.api.Assertions.assertThat

class CharsetTest {

    @Rule
    @JvmField
    var expectedException = ExpectedException.none()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun charsetUtf8() {
        val charset = Charset.forName("UTF-8")
        assertThat(charset).isNotNull()
    }

    @Test
    fun shouldThrowExceptionWhenCreatingInvalidCharset() {
        expectedException.expect(UnsupportedCharsetException::class.java)
        Charset.forName("UTF-6")
    }

}
