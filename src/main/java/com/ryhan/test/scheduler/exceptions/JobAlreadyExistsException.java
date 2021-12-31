package com.ryhan.test.scheduler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class JobAlreadyExistsException extends RuntimeException{
  public JobAlreadyExistsException(String message) {
    super(message);
  }
  public JobAlreadyExistsException(String message, Throwable cause) {
    super(message, cause);
  }
}
