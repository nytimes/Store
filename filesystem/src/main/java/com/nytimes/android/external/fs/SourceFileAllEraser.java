package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.filesystem.FileSystem;
import com.nytimes.android.external.store.base.DiskAllErase;
import com.nytimes.android.external.store.base.DiskErase;
import com.nytimes.android.external.store.base.impl.BarCode;

import okio.BufferedSource;


public class SourceFileAllEraser extends FSAllEraser implements DiskAllErase {

    public SourceFileAllEraser(FileSystem fileSystem) {
        super(fileSystem);
    }
}
