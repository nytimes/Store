package com.nytimes.android.external.fs;

import android.support.annotation.NonNull;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.IBarCode;

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
    public Observable<BufferedSource> read(@NonNull final IBarCode IBarCode) {
        return Observable.fromCallable(new Callable<BufferedSource>() {
            @NonNull
            @Override
            public BufferedSource call() throws FileNotFoundException {
                return fileSystem.read(pathForBarcode(IBarCode));
            }
        });
    }

    public boolean exists(@NonNull IBarCode IBarCode) {
        return fileSystem.exists(pathForBarcode(IBarCode));
    }
}
