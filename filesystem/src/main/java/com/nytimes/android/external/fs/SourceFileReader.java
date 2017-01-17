package com.nytimes.android.external.fs;

import android.support.annotation.NonNull;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.BarCode;
import com.nytimes.android.external.store.base.DiskRead;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

import static com.nytimes.android.external.fs.SourcePersister.pathForBarcode;


public class SourceFileReader implements DiskRead<BufferedSource> {

    final FileSystem fileSystem;

    @Inject
    public SourceFileReader(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @NonNull
    @Override
    public Observable<BufferedSource> read(@NonNull final BarCode barCode) {
        return Observable.fromCallable(new Callable<BufferedSource>() {
            @NonNull
            @Override
            public BufferedSource call() throws FileNotFoundException {
                return fileSystem.read(pathForBarcode(barCode));
            }
        });
    }

    public boolean exists(@NonNull BarCode barCode) {
        return fileSystem.exists(pathForBarcode(barCode));
    }
}
