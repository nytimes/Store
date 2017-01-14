package com.nytimes.android.external.store;

import com.google.gson.Gson;
import com.nytimes.android.external.store.middleware.GsonParserFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Type;

public class GsonParserFactoryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    Type type;
    private final Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCreateParsersProperly() throws Exception {
        GsonParserFactory.createReaderParser(gson, type);
        GsonParserFactory.createSourceParser(gson, type);
        GsonParserFactory.createStringParser(gson, type);
    }

    @Test
    public void should_ThrowException_When_CreatingReaderWithNullType() throws Exception {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createReaderParser(gson, null);
    }

    @Test
    public void should_ThrowException_When_CreatingReaderWithNullGson() throws Exception {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createReaderParser(null, type);
    }

    @Test
    public void should_ThrowException_When_CreatingSourceWithNullType() throws Exception {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createSourceParser(gson, null);
    }

    @Test
    public void should_ThrowException_When_CreatingSourceWithNullGson() throws Exception {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createSourceParser(null, type);
    }

    @Test
    public void should_ThrowException_When_CreatingStringWithNullType() throws Exception {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createStringParser(gson, null);
    }

    @Test
    public void should_ThrowException_When_CreatingStringWithNullGson() throws Exception {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.createStringParser(null, type);
    }
}
