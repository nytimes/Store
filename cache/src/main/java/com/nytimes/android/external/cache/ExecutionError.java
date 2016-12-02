package com.nytimes.android.external.cache;




public class ExecutionError extends Error {
  /**
   * Creates a new instance with the given cause.
   */
  public ExecutionError(  Error cause) {
    super(cause);
  }

  private static final long serialVersionUID = 0;
}
