package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.impl.BarCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.IOException;

import okio.BufferedSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class SourcePersisterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    FileSystem fileSystem;
    @Mock
    BufferedSource bufferedSource;

    private SourcePersister sourcePersister;
    private final BarCode simple = new BarCode("type", "key");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sourcePersister = new SourcePersister(fileSystem);
    }

    @Test
    public void readExists() throws FileNotFoundException {
        when(fileSystem.exists(simple.toString()))
                .thenReturn(true);
        when(fileSystem.read(simple.toString())).thenReturn(bufferedSource);

        BufferedSource returnedValue = sourcePersister.read(simple).blockingGet();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void readDoesNotExist() throws FileNotFoundException {
        when(fileSystem.exists(SourcePersister.Companion.pathForBarcode(simple)))
                .thenReturn(false);

        sourcePersister.read(simple).test().assertError(FileNotFoundException.class);
    }

    @Test
    public void write() throws IOException {
        assertThat(sourcePersister.write(simple, bufferedSource).blockingGet()).isTrue();
    }

    @Test
    public void pathForBarcode() {
        assertThat(SourcePersister.Companion.pathForBarcode(simple)).isEqualTo("typekey");
    }
}
