package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

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
    private final BarCodePathResolver resolver=new BarCodePathResolver();
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

        BufferedSource returnedValue = sourcePersister.read(simple).toBlocking().single();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    public void readDoesNotExist() throws FileNotFoundException {
        expectedException.expect(NoSuchElementException.class);
        when(fileSystem.exists(resolver.resolve(simple)))
                .thenReturn(false);

        sourcePersister.read(simple).toBlocking().single();
    }

    @Test
    public void write() throws IOException {
        assertThat(sourcePersister.write(simple, bufferedSource).toBlocking().single()).isTrue();
    }

    @Test
    public void pathForBarcode() {
        assertThat(resolver.resolve(simple)).isEqualTo("typekey");
    }
}
