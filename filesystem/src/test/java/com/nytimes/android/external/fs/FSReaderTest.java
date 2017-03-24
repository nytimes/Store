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
    private BarCodePathResolver barCodePathResolver=new BarCodePathResolver();

    @Test
    public void readAll() throws IOException {
        //create 2 barcodes with same type
        BarCode barCode = new BarCode(TYPE, "key");
        BarCode barCode1 = new BarCode(TYPE, "key2");

        FileSystem fileSystem = FileSystemFactory.create(createTempDir());
        //write different data to File System for each barcode
        fileSystem.write(barCodePathResolver.resolve(barCode), source(CHALLAH));
        fileSystem.write(barCodePathResolver.resolve(barCode), source(CHALLAH_CHALLAH));
        FSReader<BarCode> reader = new FSReader<>(fileSystem, barCodePathResolver);
        //read back all values for the TYPE
        BlockingObservable<BufferedSource> observable = reader.readAll(TYPE).toBlocking();
        assertThat(observable.first()).isEqualTo(CHALLAH);
        assertThat(observable.last()).isEqualTo(CHALLAH_CHALLAH);
    }

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }

}
