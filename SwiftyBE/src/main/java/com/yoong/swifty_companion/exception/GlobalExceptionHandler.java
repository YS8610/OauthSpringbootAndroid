package com.yoong.swifty_companion.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yoong.swifty_companion.model.ErrorRes;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {ApiReqException.class})
  public ResponseEntity<ErrorRes> handleApiReqException(ApiReqException ex) {
    ErrorRes errorRes = new ErrorRes(
      ex.getCode(),
      ex.getMessage(),
      System.currentTimeMillis()
    );
    return ResponseEntity.status(ex.getCode()).body(errorRes);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<ErrorRes> handleGenericException(Exception ex) {
    ErrorRes errorRes = new ErrorRes(
      500,
      "Internal Server Error",
      System.currentTimeMillis()
    );
    return ResponseEntity.status(500).body(errorRes);
  }
}
