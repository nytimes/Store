package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.RecordState;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okio.BufferedSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class RecordPersisterTest {
    private static final BarCode simple = new BarCode("type", "key");
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    FileSystem fileSystem;
    @Mock
    BufferedSource bufferedSource;
    private RecordPersister sourcePersister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sourcePersister = new RecordPersister(fileSystem, 1L, TimeUnit.DAYS);
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
    public void freshTest() {
        when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple)))
                .thenReturn(RecordState.FRESH);

        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(RecordState.FRESH);
    }

    @Test
    public void staleTest() {
        when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple)))
                .thenReturn(RecordState.STALE);

        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(RecordState.STALE);
    }

    @Test
    public void missingTest() {
        when(fileSystem.getRecordState(TimeUnit.DAYS, 1L, SourcePersister.pathForBarcode(simple)))
                .thenReturn(RecordState.MISSING);

        assertThat(sourcePersister.getRecordState(simple)).isEqualTo(RecordState.MISSING);
    }

    @Test
    public void readDoesNotExist() throws FileNotFoundException {
        when(fileSystem.exists(SourcePersister.pathForBarcode(simple)))
                .thenReturn(false);

        sourcePersister
                .read(simple)
                .test()
                .awaitTerminalEvent()
                .assertError(FileNotFoundException.class);
    }

    @Test
    public void write() throws IOException {
        assertThat(sourcePersister.write(simple, bufferedSource).toBlocking().single()).isTrue();
    }

    @Test
    public void pathForBarcode() {
        assertThat(SourcePersister.pathForBarcode(simple)).isEqualTo("typekey");
    }
}
