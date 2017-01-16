package com.nytimes.android.external.fs;

import android.support.annotation.NonNull;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.BaseBarcode;
import com.nytimes.android.external.store.base.DiskWrite;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import okio.BufferedSource;
import rx.Observable;

import static com.nytimes.android.external.fs.SourcePersister.pathForBarcode;
import static okio.Okio.buffer;

public class SourceFileWriter implements DiskWrite<BufferedSource> {

    final FileSystem fileSystem;

    @Inject
    public SourceFileWriter(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }


    @NonNull
    @Override
    public Observable<Boolean> write(@NonNull final BaseBarcode barCode, @NonNull final BufferedSource data) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @NonNull
            @Override
            @SuppressWarnings("PMD.SignatureDeclareThrowsException")
            public Boolean call() throws Exception {
                fileSystem.write(pathForBarcode(barCode), buffer(data));
                return true;
            }
        });
    }
}
