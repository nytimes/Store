package com.nytimes.android.external.fs3.filesystem

import com.nytimes.android.external.store3.base.RecordState
import okio.BufferedSource
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * a **FileSystem** provides an api to a hierarchal structure of [File]s, which does *not* necessarily
 * represent how the files are stored on disk. So, a **FileSystem** object's "/foo/bar.txt" file might actually
 * be stored as "/blarg/bloop/bleb/foo/1234567890", or in a sql database, or in memory, etc. - you get the idea.
 *
 *
 * All [File]s in a **FileSystem** are internally versioned and copy-on-write. What that means is:
 *
 *  * calling [.write] has no effect on existing readers because
 * it's actually writing to a new file
 *  * once a writer is done writing, new readers calling [.read] will get the new content
 *  * once a file version has no more readers, it is deleted
 *  * multiple writers can be writing to the same file, as internally they're actually writing to
 * different file versions. Once they are *done* writing, the most recent version becomes the "current" version
 * and all others are obsolete.
 *
 *
 *
 * There is no way for the caller to specify a particular version.
 *
 *
 * The advantage of this scheme is that you get better parallelization; the disadvantage is that multiple readers
 * can be reading different versions of the same file.
 *
 *
 * It's important to note that a file's "current" version is defined by the most recently *closed* writer, e.g.
 * <pre>
 * 1) writer A starts
 * 2) writer B starts
 * 3) writer B finishes
 * 4) writer A finishes
 *
 * --> readers will get writer A's content!
</pre> *
 */
interface FileSystem {

    /**
     * read the latest version of a file
     *
     * @param path what to read
     * @return a [BufferedSource] to read - Caller must close it!
     * @throws FileNotFoundException
     */
    @Throws(FileNotFoundException::class)
    fun read(path: String): BufferedSource

    /**
     * write a new version of a file. No readers will "see" this version until it has successfully been completely
     * written to and closed. In case of error, the version is deleted from disk.
     *
     * @param path   what to write to
     * @param source a [BufferedSource] containing the content to be written to disk. Caller must close it!
     * @throws IOException
     */
    @Throws(IOException::class)
    fun write(path: String, source: BufferedSource)

    /**
     * delete a single file. The file data won't *really* be deleted until all readers are done reading.
     *
     * @param path what to delete - must correspond to a single file, not a directory
     * @throws IOException
     */
    @Throws(IOException::class)
    fun delete(path: String)

    /**
     * delete a directory, recursively.
     * The files' data won't *really* be deleted until all readers are done reading.
     *
     * @param path what to delete - must correspond to a directory, not a single file
     * @throws IOException
     */
    @Throws(IOException::class)
    fun deleteAll(path: String)

    /**
     * list all files under a given directory, recursively.
     */
    @Throws(FileNotFoundException::class)
    fun list(path: String): Collection<String>

    /**
     * does this file exist?
     *
     * @param file what to test for
     * @return exists, duh
     */
    fun exists(file: String): Boolean

    /**
     * compares age of file with given expiration time and returns
     * appropriate recordState
     */
    fun getRecordState(expirationUnit: TimeUnit,
                       expirationDuration: Long,
                       path: String): RecordState
}
