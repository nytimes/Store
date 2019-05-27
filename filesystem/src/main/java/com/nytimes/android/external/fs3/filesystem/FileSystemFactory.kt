package com.nytimes.android.external.fs3.filesystem

import java.io.File
import java.io.IOException

/**
 * Factory for [FileSystem].
 */
object FileSystemFactory {

    /**
     * Creates new instance of [FileSystemImpl].
     *
     * @param root root directory.
     * @return new instance of [FileSystemImpl].
     * @throws IOException
     */
    @Throws(IOException::class)
    fun create(root: File): FileSystem = FileSystemImpl(root)
}
