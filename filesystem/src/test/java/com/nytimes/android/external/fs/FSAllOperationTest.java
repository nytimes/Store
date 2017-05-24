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

public class FSAllOperationTest {

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
        BarCode barCode = new BarCode(TYPE, "key.txt");
        BarCode barCode1 = new BarCode(TYPE, "key2.txt");

        File tempDir = createTempDir();
        FileSystem fileSystem = FileSystemFactory.create(tempDir);

        //write different data to File System for each barcode
        fileSystem.write("type/key.txt", source(CHALLAH));
        fileSystem.write("type/key2.txt", source(CHALLAH_CHALLAH));
        FSAllReader<BarCode> reader = new FSAllReader<>(fileSystem);
        //read back all values for the TYPE
        BlockingObservable<BufferedSource> observable = reader.readAll("type").toBlocking();
        assertThat(observable.first().readUtf8()).isEqualTo(CHALLAH);
        assertThat(observable.last().readUtf8()).isEqualTo(CHALLAH_CHALLAH);
    }

    @Test
    public void deleteAll() throws IOException {
        //create 2 barcodes with same type
        BarCode barCode = new BarCode(TYPE, "key.txt");
        BarCode barCode1 = new BarCode(TYPE, "key2.txt");

        File tempDir = createTempDir();
        FileSystem fileSystem = FileSystemFactory.create(tempDir);

        //write different data to File System for each barcode
        fileSystem.write("type/key.txt", source(CHALLAH));
        fileSystem.write("type/key2.txt", source(CHALLAH_CHALLAH));
        FSAllEraser eraser = new FSAllEraser(fileSystem);
        BlockingObservable<Boolean> observable = eraser.deleteAll("type").toBlocking();
        assertThat(observable.first()).isEqualTo(true);
        assertThat(observable.last()).isEqualTo(true);
    }

}
