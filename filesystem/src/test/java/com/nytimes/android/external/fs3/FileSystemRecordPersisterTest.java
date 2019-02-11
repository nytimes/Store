package com.nytimes.android.external.fs3;

import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.store3.base.RecordState;
import com.nytimes.android.external.store3.base.impl.BarCode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okio.BufferedSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

public class FileSystemRecordPersisterTest {

    @Mock
    FileSystem fileSystem;
    @Mock
    BufferedSource bufferedSource;

    private final BarCode simple = new BarCode("type", "key");
    private final String resolvedPath = new BarCodePathResolver().resolve(simple);
    private FileSystemRecordPersister<BarCode> fileSystemPersister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fileSystemPersister = FileSystemRecordPersister.Companion.create(fileSystem,
                new BarCodePathResolver(),
                1, TimeUnit.DAYS);
    }

    @Test
    public void readExists() throws FileNotFoundException {
        when(fileSystem.exists(resolvedPath))
                .thenReturn(true);
        when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);

        BufferedSource returnedValue = fileSystemPersister.read(simple).blockingGet();
        assertThat(returnedValue).isEqualTo(bufferedSource);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void readDoesNotExist() throws FileNotFoundException {
        when(fileSystem.exists(resolvedPath))
                .thenReturn(false);

        fileSystemPersister.read(simple).test().assertError(FileNotFoundException.class);
    }

    @Test
    @SuppressWarnings("CheckReturnValue")
    public void writeThenRead() throws IOException {
        when(fileSystem.read(resolvedPath)).thenReturn(bufferedSource);
        when(fileSystem.exists(resolvedPath)).thenReturn(true);
        fileSystemPersister.write(simple, bufferedSource).blockingGet();
        BufferedSource source = fileSystemPersister.read(simple).blockingGet();
        InOrder inOrder = inOrder(fileSystem);
        inOrder.verify(fileSystem).write(resolvedPath, bufferedSource);
        inOrder.verify(fileSystem).exists(resolvedPath);
        inOrder.verify(fileSystem).read(resolvedPath);

        assertThat(source).isEqualTo(bufferedSource);


    }

    @Test
    public void freshTest() {
        when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, resolvedPath))
                .thenReturn(RecordState.FRESH);

        assertThat(fileSystemPersister.getRecordState(simple)).isEqualTo(RecordState.FRESH);
    }

    @Test
    public void staleTest() {
        when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, resolvedPath))
                .thenReturn(RecordState.STALE);

        assertThat(fileSystemPersister.getRecordState(simple)).isEqualTo(RecordState.STALE);
    }

    @Test
    public void missingTest() {
        when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, resolvedPath))
                .thenReturn(RecordState.MISSING);

        assertThat(fileSystemPersister.getRecordState(simple)).isEqualTo(RecordState.MISSING);
    }


}
