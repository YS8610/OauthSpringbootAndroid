package com.yoong.swifty_companion.exception;

public class ApiReqException extends RuntimeException {
  private final int code;

  public ApiReqException(String message, int code) {
    super(message);
    this.code = code;
  }

  public ApiReqException(String message, Throwable cause, int code) {
    super(message, cause);
    this.code = code;
  }

  public int getCode() {
    return code;
  }

}
