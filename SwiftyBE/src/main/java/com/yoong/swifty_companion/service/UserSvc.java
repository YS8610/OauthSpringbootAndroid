package com.yoong.swifty_companion.service;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.yoong.swifty_companion.SwiftyConstants;
import com.yoong.swifty_companion.exception.ApiReqException;
import com.yoong.swifty_companion.model.OauthTokenInfo;
import com.yoong.swifty_companion.model.UserDTO;

import jakarta.servlet.http.HttpSession;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserSvc {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final Map<String, OauthTokenInfo> inMemoryUserStore;
  private final TokenSvc tokenSvc;

  public UserSvc(RestTemplate restTemplate, ObjectMapper objectMapper, Map<String, OauthTokenInfo> inMemoryUserStore, TokenSvc tokenSvc) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.inMemoryUserStore = inMemoryUserStore;
    this.tokenSvc = tokenSvc;
  }

  public UserDTO getUserInfo(String accessToken, HttpSession session) {
    OauthTokenInfo tokenInfo = inMemoryUserStore.get(accessToken);
    // if (tokenInfo == null)
    //   return null;
    if (tokenInfo != null && tokenSvc.isTokenExpired(tokenInfo)) {
      String newAccessToken = tokenSvc.refreshToken(tokenInfo.rfToken(), session);
      if (newAccessToken == null)
        throw new ApiReqException("unable to refresh expired access token", HttpStatus.BAD_GATEWAY.value());
      accessToken = newAccessToken;
    }
    URI me42Uri = URI.create(SwiftyConstants.API_42_API_URL + "me");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Accept", "application/json");
    headers.set("Authorization", "Bearer " + accessToken);
    // execute request
    RequestEntity<Void> req = RequestEntity.get(me42Uri).headers(headers).build();
    ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
    if (resp.getStatusCode().is2xxSuccessful()){
      UserDTO user = objectMapper.readValue(resp.getBody(), UserDTO.class);
      return user;
    }
    throw new ApiReqException("error fetching user info", HttpStatus.BAD_GATEWAY.value());
  }

  public String isUserAuthenticated(String accessToken, HttpSession session) {
    if (accessToken == null)
      return null;
    OauthTokenInfo tokenInfo = inMemoryUserStore.get(accessToken);
    if (tokenInfo != null && tokenSvc.isTokenExpired(tokenInfo)) {
      String newAccessToken = tokenSvc.refreshToken(tokenInfo.rfToken(), session);
      if (newAccessToken == null)
        return null;
      accessToken = newAccessToken;
    }
    HttpHeaders reqHeaders = new HttpHeaders();
    reqHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    reqHeaders.set("Accept", "application/json");
    reqHeaders.set("Authorization", "Bearer " + accessToken);
    RequestEntity<Void> req = RequestEntity.get(SwiftyConstants.API_42_COALITIONS).headers(reqHeaders).build();
    ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
    if (resp.getStatusCode().is2xxSuccessful())
      return accessToken;
    session.removeAttribute(accessToken);
    return null;
  }

}
