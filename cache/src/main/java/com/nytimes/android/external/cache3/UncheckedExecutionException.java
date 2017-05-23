package com.nytimes.android.external.cache3;

public class UncheckedExecutionException extends RuntimeException {
// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Creates a new instance with {@code null} as its detail message.
//   */
//  protected UncheckedExecutionException() {}
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Creates a new instance with the given detail message.
//   */
//  protected UncheckedExecutionException(  String message) {
//    super(message);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

// --Commented out by Inspection START (11/29/16, 5:04 PM):
//  /**
//   * Creates a new instance with the given detail message and cause.
//   */
//  public UncheckedExecutionException(  String message,   Throwable cause) {
//    super(message, cause);
//  }
// --Commented out by Inspection STOP (11/29/16, 5:04 PM)

  /**
   * Creates a new instance with the given cause.
   */
  public UncheckedExecutionException(  Throwable cause) {
    super(cause);
  }

  private static final long serialVersionUID = 0;
}
