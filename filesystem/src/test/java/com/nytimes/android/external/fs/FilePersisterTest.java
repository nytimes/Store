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

import okio.BufferedSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class FilePersisterTest {
    private static final BarCode simple = new BarCode("type", "key");
    private static final String resolvedPath = new BarCodePathResolver().resolve(simple);
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    FileSystem fileSystem;
    @Mock
    BufferedSource bufferedSource;
    private Persister<BufferedSource, BarCode> fileSystemPersister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fileSystemPersister = FileSystemPersister.create(fileSystem, new BarCodePathResolver());
    }

    @Test
    public void readExists() throws FileNotFoundException {
        when(fileSystem.exists(resolvedPath))
                .thenReturn(true);
        when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);

        BufferedSource returnedValue = fileSystemPersister.read(simple).toBlocking().single();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    public void readDoesNotExist() {
        when(fileSystem.exists(resolvedPath))
                .thenReturn(false);
        fileSystemPersister.read(simple).test().awaitTerminalEvent().assertError(FileNotFoundException.class);
    }

    @Test
    public void writeThenRead() throws IOException {
        when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);
        when(fileSystem.exists(resolvedPath)).thenReturn(true);
        fileSystemPersister.write(simple, bufferedSource).toBlocking().single();
        BufferedSource source = fileSystemPersister.read(simple).toBlocking().first();
        InOrder inOrder = inOrder(fileSystem);
        inOrder.verify(fileSystem).write(resolvedPath, bufferedSource);
        inOrder.verify(fileSystem).exists(resolvedPath);
        inOrder.verify(fileSystem).read(resolvedPath);

        assertThat(source).isEqualTo(bufferedSource);
    }

}
