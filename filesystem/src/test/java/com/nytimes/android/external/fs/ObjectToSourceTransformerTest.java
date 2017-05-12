package com.nytimes.android.external.fs;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import okio.BufferedSource;
import rx.Observable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ObjectToSourceTransformerTest {

    @Mock
    BufferedSourceAdapter<String> mockBufferedParser;

    @Mock
    BufferedSource mockBufferedSource;

    @Before
    public void setUp() throws Exception {
        when(mockBufferedParser.toJson(any())).thenReturn(mockBufferedSource);
    }

    @Test
    public void testTransformer() throws Exception {
        BufferedSource source = Observable.just("test")
                .compose(new ObjectToSourceTransformer<>(mockBufferedParser))
                .toBlocking()
                .first();

        assertEquals(source, mockBufferedSource);
    }
}
