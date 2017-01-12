package com.nytimes.android.external.fs.filesystem;

import android.support.annotation.NonNull;

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
  @NonNull
  public static FileSystem create(@NonNull File root) throws IOException {
    return new FileSystemImpl(root);
  }
}
