package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.Persister;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;

import okio.BufferedSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class FilePersisterTest {

    public static final String FILENAME_PREFIX = "TEST";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    FileSystem fileSystem;
    @Mock
    BufferedSource bufferedSource;

    private final BarCode simple = new BarCode("type", "key");
    private Persister<BufferedSource, BarCode> fileSystemPersister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fileSystemPersister = FileSystemPersister.create(fileSystem, FILENAME_PREFIX);
    }

    @Test
    public void readExists() throws FileNotFoundException {
        when(fileSystem.exists(FILENAME_PREFIX + simple))
                .thenReturn(true);
        when(fileSystem.read(FILENAME_PREFIX + simple)).thenReturn(bufferedSource);

        BufferedSource returnedValue = fileSystemPersister.read(simple).toBlocking().single();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    public void readDoesNotExist() throws FileNotFoundException {
        expectedException.expect(NoSuchElementException.class);
        when(fileSystem.exists(SourcePersister.pathForBarcode(simple)))
                .thenReturn(false);

        fileSystemPersister.read(simple).toBlocking().single();
    }

    @Test
    public void writeThenRead() throws IOException {
        when(fileSystem.read(FILENAME_PREFIX + simple)).thenReturn(bufferedSource);
        when(fileSystem.exists(FILENAME_PREFIX + simple)).thenReturn(true);
        fileSystemPersister.write(simple, bufferedSource).toBlocking().single();
        BufferedSource source = fileSystemPersister.read(simple).toBlocking().first();
        InOrder inOrder = inOrder(fileSystem);
        inOrder.verify(fileSystem).write(FILENAME_PREFIX + simple, bufferedSource);
        inOrder.verify(fileSystem).exists(FILENAME_PREFIX + simple);
        inOrder.verify(fileSystem).read(FILENAME_PREFIX + simple);

        assertThat(source).isEqualTo(bufferedSource);



    }

}
