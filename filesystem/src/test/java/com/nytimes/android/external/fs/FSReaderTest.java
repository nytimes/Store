package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.fs.filesystem.FileSystemFactory;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import okio.BufferedSource;
import okio.Okio;
import rx.observables.BlockingObservable;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.createTempDir;
import static org.assertj.core.api.Assertions.assertThat;

public class FSReaderTest {

    public static final String TYPE = "type";
    public static final String CHALLAH = "Challah";
    public static final String CHALLAH_CHALLAH = "Challah_CHALLAH";
    private BarCodeReadAllPathResolver barCodePathResolver = new BarCodeReadAllPathResolver();

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }

    @Test
    public void readAll() throws IOException {
        //create 2 barcodes with same type
        BarCode barCode = new BarCode(TYPE, "key");
        BarCode barCode1 = new BarCode(TYPE, "key2");

        FileSystem fileSystem = FileSystemFactory.create(createTempDir());
        //write different data to File System for each barcode
        fileSystem.write(barCodePathResolver.resolve(barCode), source(CHALLAH));
        fileSystem.write(barCodePathResolver.resolve(barCode1), source(CHALLAH_CHALLAH));
        FSReader<BarCode> reader = new FSReader<>(fileSystem, barCodePathResolver);
        //read back all values for the TYPE
        BlockingObservable<BufferedSource> observable = reader.readAll(barCode).toBlocking();
        assertThat(observable.first().readUtf8()).isEqualTo(CHALLAH);
        assertThat(observable.last().readUtf8()).isEqualTo(CHALLAH_CHALLAH);
    }

}
