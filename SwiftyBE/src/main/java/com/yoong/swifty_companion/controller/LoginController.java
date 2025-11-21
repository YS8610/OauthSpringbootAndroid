package com.yoong.swifty_companion.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.yoong.swifty_companion.SwiftyConstants;
import com.yoong.swifty_companion.config.ConfigProperties;
import com.yoong.swifty_companion.exception.ApiReqException;
import com.yoong.swifty_companion.model.OauthToken;
import com.yoong.swifty_companion.model.OauthTokenInfo;
import com.yoong.swifty_companion.service.TokenSvc;
import com.yoong.swifty_companion.service.UserSvc;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/oauth")
public class LoginController {

  private final ConfigProperties configProperties;
  private final TokenSvc tokenSvc;
  private final UserSvc userSvc;
  private final Map<String, OauthTokenInfo> userStore;

  public LoginController(ConfigProperties configProperties, TokenSvc tokenSvc, UserSvc userSvc,
      Map<String, OauthTokenInfo> userStore) {
    this.configProperties = configProperties;
    this.tokenSvc = tokenSvc;
    this.userSvc = userSvc;
    this.userStore = userStore;
  }

  @GetMapping("/login")
  public ResponseEntity<Void> login(HttpSession session, @RequestHeader Map<String, String> headers) {
    String state = UUID.randomUUID().toString();
    String accessToken = (String) session.getAttribute("access_token");
    String tkn = userSvc.isUserAuthenticated(accessToken, session);
    // user already authenticated, redirect to app with token
    if (tkn != null) {
      // user is authenticated, redirect to app with token
      String tokenToUse = tkn != null ? tkn : accessToken;
      System.out.println("User already authenticated, access token: " + tokenToUse);
      URI uri = URI.create("com.yoong.swiftycompanion://auth/callback?token=" + tokenToUse);
      return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }
    // not authenticated, store state in session, do oauth redirect
    session.removeAttribute(accessToken);
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

  // @GetMapping("/callback")
  // public ResponseEntity<ResMsg> oauthCallback(@RequestParam("code") String
  // code,
  // @RequestParam("state") String returnedState, HttpSession session) {
  // String expected = (String) session.getAttribute("oauth_state");
  // session.removeAttribute("oauth_state"); // avoid replay
  // if (expected == null || !expected.equals(returnedState))
  // throw new ApiReqException("invalid state parameter",
  // HttpStatus.BAD_REQUEST.value());
  // OauthToken oauthToken = tokenSvc.getAccessToken(code);
  // System.out.println("Token response body: " + oauthToken);
  // // parse JSON response and store access_token in session
  // String accessToken = oauthToken.access_token();
  // userStore.put(accessToken, new OauthTokenInfo(oauthToken.expires_in()
  // +oauthToken.created_at(), oauthToken.refresh_token()));
  // session.setAttribute("access_token", accessToken);
  // System.out.println("Stored access_token in session " + accessToken);
  // System.out.println("Stored refresh_token in session " +
  // oauthToken.refresh_token());
  // return ResponseEntity.ok().body(new ResMsg(accessToken));
  // }

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
    userStore.put(accessToken,
        new OauthTokenInfo(oauthToken.expires_in() + oauthToken.created_at(), oauthToken.refresh_token()));
    session.setAttribute("access_token", accessToken);
    System.out.println("Stored access_token in session " + accessToken);
    System.out.println("Stored refresh_token in session " + oauthToken.refresh_token());
    URI uri = URI.create("com.yoong.swiftycompanion://auth/callback?token=" + accessToken);
    return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
  }
}
