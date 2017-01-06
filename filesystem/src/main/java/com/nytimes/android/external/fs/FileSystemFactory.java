package com.nytimes.android.external.fs;

import com.nytimes.android.external.fs.impl.FileSystemImpl;
import java.io.File;
import java.io.IOException;

/**
 * Factory for {@link FileSystem}.
 */
public class FileSystemFactory {

  /**
   * Creates new instance of {@link FileSystemImpl}.
   *
   * @param root root directory.
   * @return new instance of {@link FileSystemImpl}.
   * @throws IOException
   */
  public static FileSystem create(File root) throws IOException {
    return new FileSystemImpl(root);
  }
}
