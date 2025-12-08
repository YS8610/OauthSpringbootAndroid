package com.yoong.swifty_companion.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.yoong.swifty_companion.SwiftyConstants;
import com.yoong.swifty_companion.config.ConfigProperties;
import com.yoong.swifty_companion.exception.ApiReqException;
import com.yoong.swifty_companion.model.OauthToken;
import com.yoong.swifty_companion.service.TokenSvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/oauth")
public class LoginController {

  private final ConfigProperties configProperties;
  private final TokenSvc tokenSvc;


  public LoginController(ConfigProperties configProperties, TokenSvc tokenSvc) {
    this.configProperties = configProperties;
    this.tokenSvc = tokenSvc;
  }

  @GetMapping("/login")
  public ResponseEntity<Void> login(HttpSession session, @RequestHeader Map<String, String> headers) {
    String state = UUID.randomUUID().toString();
    session.setAttribute("oauth_state", state);
    System.out.println("Generated OAuth state: " + state);
    String redirect = UriComponentsBuilder.fromUriString(SwiftyConstants.API_42_OAUTH_URL)
        .queryParam("client_id", configProperties.clientId())
        .queryParam("redirect_uri", SwiftyConstants.API_42_OAUTH_REDIRECT)
        .queryParam("response_type", "code")
        .queryParam("scope", "public")
        .queryParam("state", state)
        .build()
        .toUriString();
    return ResponseEntity.status(302)
        .location(URI.create(redirect))
        .build();
  }

  @GetMapping("/callback")
  public ResponseEntity<Void> oauthCallback(@RequestParam("code") String code,
      @RequestParam("state") String returnedState, HttpSession session) {
    String expected = (String) session.getAttribute("oauth_state");
    session.removeAttribute("oauth_state"); // avoid replay
    if (expected == null || !expected.equals(returnedState))
      throw new ApiReqException("invalid state parameter", HttpStatus.BAD_REQUEST.value());
    OauthToken oauthToken = tokenSvc.getAccessToken(code);
    System.out.println("Token response body: " + oauthToken);
    // parse JSON response and store access_token in session
    String accessToken = oauthToken.access_token();
    // session.setAttribute("access_token", accessToken);
    // session.setAttribute("refresh_token", oauthToken.refresh_token());
    System.out.println("access_token in session " + accessToken);
    System.out.println("refresh_token in session " + oauthToken.refresh_token());
    URI uri = URI.create(
        SwiftyConstants.DEEP_LINK_CALLBACK + "?token=" + accessToken + "&refresh_token=" + oauthToken.refresh_token());
    return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<OauthToken> refreshToken(@RequestBody Map<String, String> body) {
    String refreshToken = body.get("refresh_token");
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new ApiReqException("Missing refresh_token in request body", HttpStatus.BAD_REQUEST.value());
    }
    OauthToken newToken = tokenSvc.refreshToken(refreshToken);
    return ResponseEntity.ok(newToken);
  }
}
