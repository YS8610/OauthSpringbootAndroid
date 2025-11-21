package com.yoong.swifty_companion.model;

public record ErrorRes(
  int code,
  String message,
  Long timestamp
  ) {}
