package com.nytimes.android.external.fs3;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Single;
import okio.BufferedSource;

import static org.assertj.core.api.Assertions.assertThat;
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
        BufferedSource source = Single.just("test")
                .compose(new ObjectToSourceTransformer<>(mockBufferedParser))
                .blockingGet();

        assertThat(source).isEqualTo(mockBufferedSource);
    }
}
