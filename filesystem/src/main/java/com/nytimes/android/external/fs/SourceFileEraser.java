package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskErase;
import com.nytimes.android.external.store.base.impl.BarCode;

import okio.BufferedSource;


public class SourceFileEraser extends FSEraser<BarCode> implements DiskErase<BufferedSource, BarCode> {

    public SourceFileEraser(FileSystem fileSystem) {
        this(fileSystem, new BarCodePathResolver());
    }

    public SourceFileEraser(FileSystem fileSystem, PathResolver<BarCode> pathResolver) {
        super(fileSystem, pathResolver);
    }
}