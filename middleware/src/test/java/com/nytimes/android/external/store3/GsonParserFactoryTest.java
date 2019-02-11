package com.nytimes.android.external.store3;

import com.google.gson.Gson;
import com.nytimes.android.external.store3.middleware.GsonParserFactory;

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
    public void shouldCreateParsersProperly() {
        GsonParserFactory.INSTANCE.createReaderParser(gson, type);
        GsonParserFactory.INSTANCE.createSourceParser(gson, type);
        GsonParserFactory.INSTANCE.createStringParser(gson, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingReaderWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.INSTANCE.createReaderParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingReaderWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.INSTANCE.createReaderParser(null, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSourceWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.INSTANCE.createSourceParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingSourceWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.INSTANCE.createSourceParser(null, type);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingStringWithNullType() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.INSTANCE.createStringParser(gson, null);
    }

    @Test
    public void shouldThrowExceptionWhenCreatingStringWithNullGson() {
        expectedException.expect(NullPointerException.class);
        GsonParserFactory.INSTANCE.createStringParser(null, type);
    }
}
