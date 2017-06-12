package com.nytimes.android.external.store;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.MockitoAnnotations;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import static org.assertj.core.api.Assertions.assertThat;

public class CharsetTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void charsetUtf8() {
        Charset charset = Charset.forName("UTF-8");
        assertThat(charset).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWhenCreatingInvalidCharset() {
        expectedException.expect(UnsupportedCharsetException.class);
        Charset.forName("UTF-6");
    }


}
