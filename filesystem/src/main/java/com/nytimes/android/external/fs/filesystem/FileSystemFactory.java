package com.nytimes.android.external.fs.filesystem;

import org.jetbrains.annotations.NotNull;

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
  @NotNull
  public static FileSystem create(@NotNull File root) throws IOException {
    return new FileSystemImpl(root);
  }
}
