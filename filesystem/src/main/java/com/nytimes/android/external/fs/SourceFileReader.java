package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskRead;
import com.nytimes.android.external.store.base.impl.BarCode;

import org.jetbrains.annotations.NotNull;

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

    @NotNull
    @Override
    public Observable<BufferedSource> read(@NotNull final BarCode barCode) {
        return Observable.fromCallable(new Callable<BufferedSource>() {
            @NotNull
            @Override
            public BufferedSource call() throws FileNotFoundException {
                return fileSystem.read(pathForBarcode(barCode));
            }
        });
    }

    public boolean exists(@NotNull BarCode barCode) {
        return fileSystem.exists(pathForBarcode(barCode));
    }
}
