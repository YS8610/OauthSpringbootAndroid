package com.yoong.swifty_companion.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;

import com.yoong.swifty_companion.exception.ApiReqException;
import com.yoong.swifty_companion.model.UserDTO;
import com.yoong.swifty_companion.service.UserSvc;


@RestController
@RequestMapping("/api/users")
public class UserController{
  private final UserSvc userSvc;

  public UserController(UserSvc userSvc) {
    this.userSvc = userSvc;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDTO> me(@RequestHeader Map<String, String> headers, HttpSession session) {
    String token = headers.getOrDefault("Authorization", null);
    if (token == null)
      throw new ApiReqException("unauthorized. pls login", HttpStatus.UNAUTHORIZED.value());
    UserDTO userInfo = userSvc.getUserInfo(token.substring(7), session);
    if (userInfo == null)
      throw new ApiReqException("error fetching user info", HttpStatus.BAD_GATEWAY.value());
    // System.out.println("user info fetched: " + userInfo);
    return ResponseEntity.ok(userInfo);
  }

  // @GetMapping("/me")
  // public ResponseEntity<UserDTO> me(HttpSession session) {
  //   String token = (String) session.getAttribute("access_token");
  //   if (token == null)
  //     throw new ApiReqException("unauthorized. pls login", HttpStatus.UNAUTHORIZED.value());
  //   UserDTO userInfo = userSvc.getUserInfo(token);
  //   if (userInfo == null)
  //     throw new ApiReqException("error fetching user info", HttpStatus.BAD_GATEWAY.value());
  //   return ResponseEntity.ok(userInfo);
  // }
}
