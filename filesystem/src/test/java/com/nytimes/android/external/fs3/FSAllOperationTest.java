package com.nytimes.android.external.fs3;


import com.nytimes.android.external.fs3.filesystem.FileSystem;
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import okio.BufferedSource;
import okio.Okio;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Files.createTempDir;
import static org.assertj.core.api.Assertions.assertThat;

public class FSAllOperationTest {

    public static final String FOLDER = "type";
    public static final String INNER_FOLDER = "type2";
    public static final String CHALLAH = "Challah";
    public static final String CHALLAH_CHALLAH = "Challah_CHALLAH";


    private static BufferedSource source(String data) {
        return Okio.buffer(Okio.source(new ByteArrayInputStream(data.getBytes(UTF_8))));
    }

    @Test
    public void readAll() throws IOException {
        File tempDir = createTempDir();
        FileSystem fileSystem = FileSystemFactory.create(tempDir);

        //write different data to File System for each barcode
        fileSystem.write(FOLDER + "/key.txt", source(CHALLAH));
        fileSystem.write(FOLDER + "/" + INNER_FOLDER + "/key2.txt", source(CHALLAH_CHALLAH));
        FSAllReader reader = new FSAllReader(fileSystem);
        //read back all values for the FOLDER
        Observable<BufferedSource> observable = reader.readAll(FOLDER);
        assertThat(observable.blockingFirst().readUtf8()).isEqualTo(CHALLAH);
        assertThat(observable.blockingLast().readUtf8()).isEqualTo(CHALLAH_CHALLAH);
    }

    @Test
    public void deleteAll() throws IOException {
        File tempDir = createTempDir();
        FileSystem fileSystem = FileSystemFactory.create(tempDir);
        //write different data to File System for each barcode
        fileSystem.write(FOLDER + "/key.txt", source(CHALLAH));
        fileSystem.write(FOLDER + "/" + INNER_FOLDER + "/key2.txt", source(CHALLAH_CHALLAH));

        FSAllEraser eraser = new FSAllEraser(fileSystem);
        Observable<Boolean> observable = eraser.deleteAll(FOLDER);
        assertThat(observable.blockingFirst()).isEqualTo(true);
    }

}
