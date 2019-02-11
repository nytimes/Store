package com.nytimes.android.external.fs3

import com.nytimes.android.external.fs3.filesystem.FileSystem
import com.nytimes.android.external.store3.base.DiskWrite
import com.nytimes.android.external.store3.base.impl.BarCode

import okio.BufferedSource

class SourceFileWriter @JvmOverloads
constructor(fileSystem: FileSystem, pathResolver:
PathResolver<BarCode> = BarCodePathResolver()) :
        FSWriter<BarCode>(fileSystem, pathResolver), DiskWrite<BufferedSource, BarCode>
