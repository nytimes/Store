package com.nytimes.android.sample.cache;

import android.support.annotation.Nullable;

public class ExecutionError extends Error {
// --Commented out by Inspection START (11/29/16, 5:03 PM):
//  /**
//   * Creates a new instance with {@code null} as its detail message.
//   */
//  protected ExecutionError() {}
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)

// --Commented out by Inspection START (11/29/16, 5:03 PM):
//  /**
//   * Creates a new instance with the given detail message.
//   */
//  protected ExecutionError(@Nullable String message) {
//    super(message);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)

// --Commented out by Inspection START (11/29/16, 5:03 PM):
//  /**
//   * Creates a new instance with the given detail message and cause.
//   */
//  public ExecutionError(@Nullable String message, @Nullable Error cause) {
//    super(message, cause);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:03 PM)

  /**
   * Creates a new instance with the given cause.
   */
  public ExecutionError(@Nullable Error cause) {
    super(cause);
  }

  private static final long serialVersionUID = 0;
}
