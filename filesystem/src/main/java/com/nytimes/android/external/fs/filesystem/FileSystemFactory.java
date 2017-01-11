package com.nytimes.android.external.fs.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * Factory for {@link FileSystem}.
 */
public final class FileSystemFactory {
  private FileSystemFactory() {
  }

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
