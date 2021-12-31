package com.ryhan.test.scheduler.exceptions;

public class JobNotFoundException extends RuntimeException{
  public JobNotFoundException(String message) {
    super(message);
  }
  public JobNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
