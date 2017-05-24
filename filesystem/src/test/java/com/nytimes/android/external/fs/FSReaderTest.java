package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.fs.filesystem.FileSystemFactory;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import okio.BufferedSource;
import okio.Okio;
import rx.observables.BlockingObservable;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.createTempDir;
import static org.assertj.core.api.Assertions.assertThat;

public class FSReaderTest {

    public static final String TYPE = "typee";
    public static final String CHALLAH = "Challah";
    public static final String CHALLAH_CHALLAH = "Challah_CHALLAH";
    private final BarCodeReadAllPathResolver barCodePathResolver = new BarCodeReadAllPathResolver();
    private final BarCodePathResolver barCodeWritePathResolver = new BarCodePathResolver();

    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }

    @Test
    public void readAll() throws IOException {
        //create 2 barcodes with same type
        BarCode barCode = new BarCode(TYPE, "keyy.txt");
        BarCode barCode1 = new BarCode(TYPE, "key2.txt");

        File tempDir = createTempDir();
        String str = tempDir.getAbsolutePath();
        System.out.println(str);
        FileSystem fileSystem = FileSystemFactory.create(tempDir);

        //write different data to File System for each barcode
        fileSystem.write(barCodeWritePathResolver.resolve(barCode), source(CHALLAH));
        fileSystem.write(barCodeWritePathResolver.resolve(barCode1), source(CHALLAH_CHALLAH));
        FSAllReader<BarCode> reader = new FSAllReader<>(fileSystem);
        //read back all values for the TYPE
        BlockingObservable<BufferedSource> observable = reader.readAll(barCodePathResolver.resolve(barCode)).toBlocking();
        assertThat(observable.first().readUtf8()).isEqualTo(CHALLAH_CHALLAH);
        assertThat(observable.last().readUtf8()).isEqualTo(CHALLAH);
    }

}
