package com.nytimes.android.external.fs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import okio.BufferedSource;

/**
 * a <b>FileSystem</b> provides an api to a hierarchal structure of {@link File}s, which does *not* necessarily
 * represent how the files are stored on disk. So, a <b>FileSystem</b> object's "/foo/bar.txt" file might actually
 * be stored as "/blarg/bloop/bleb/foo/1234567890", or in a sql database, or in memory, etc. - you get the idea.
 * <p>
 * All {@link File}s in a <b>FileSystem</b> are internally versioned and copy-on-write. What that means is:
 * <ul>
 * <li>calling {@link #write(String, BufferedSource)} has no effect on existing readers because
 * it's actually writing to a new file</li>
 * <li>once a writer is done writing, new readers calling {@link #read(String)} will get the new content</li>
 * <li>once a file version has no more readers, it is deleted</li>
 * <li>multiple writers can be writing to the same file, as internally they're actually writing to
 * different file versions. Once they are *done* writing, the most recent version becomes the "current" version
 * and all others are obsolete.</li>
 * </ul>
 * <p>
 * There is no way for the caller to specify a particular version.
 * <p>
 * The advantage of this scheme is that you get better parallelization; the disadvantage is that multiple readers
 * can be reading different versions of the same file.
 * <p>
 * It's important to note that a file's "current" version is defined by the most recently *closed* writer, e.g.
 * <pre>
 *   1) writer A starts
 *   2) writer B starts
 *   3) writer B finishes
 *   4) writer A finishes
 *
 *   --> readers will get writer A's content!
 *   </pre>
 */
public interface FileSystem {

    /**
     * read the latest version of a file
     *
     * @param path what to read
     * @return a {@link BufferedSource} to read - Caller must close it!
     * @throws FileNotFoundException
     */
    BufferedSource read(String path) throws FileNotFoundException;

    /**
     * write a new version of a file. No readers will "see" this version until it has successfully been completely
     * written to and closed. In case of error, the version is deleted from disk.
     *
     * @param path what to write to
     * @param source a {@link BufferedSource} containing the content to be written to disk. Caller must close it!
     * @throws IOException
     */
    void write(String path, BufferedSource source) throws IOException;

    /**
     * delete a single file. The file data won't *really* be deleted until all readers are done reading.
     *
     * @param path what to delete - must correspond to a single file, not a directory
     * @throws IOException
     */
    void delete(String path) throws IOException;

    /**
     * delete a directory, recursively.
     * The files' data won't *really* be deleted until all readers are done reading.
     *
     * @param path what to delete - must correspond to a directory, not a single file
     * @throws IOException
     */
    void deleteAll(String path) throws IOException;

    /**
     * list all files under a given directory, recursively.
     */
    Collection<String> list(String path) throws FileNotFoundException;

    /**
     * does this file exist?
     *
     * @param file what to test for
     * @return exists, duh
     */
    boolean exists(String file);
}
