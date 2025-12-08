package com.yoong.swifty_companion.service;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.yoong.swifty_companion.SwiftyConstants;
import com.yoong.swifty_companion.config.ConfigProperties;
import com.yoong.swifty_companion.exception.ApiReqException;
import com.yoong.swifty_companion.model.OauthToken;
import com.yoong.swifty_companion.model.OauthTokenInfo;


@Service
public class TokenSvc {
  private final ConfigProperties configProperties;
  private final RestTemplate restTemplate;

  public TokenSvc(ConfigProperties configProperties, RestTemplate restTemplate) {
    this.configProperties = configProperties;
    this.restTemplate = restTemplate;
  }

  public OauthToken getAccessToken(String code) {
    URI tokenUri = URI.create(SwiftyConstants.API_42_TOKEN_URL);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Accept", "application/json");
    LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", configProperties.clientId());
    form.add("client_secret", configProperties.clientSecret());
    form.add("code", code);
    form.add("redirect_uri", SwiftyConstants.API_42_OAUTH_REDIRECT);
    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(form, headers);
    // execute request
    ResponseEntity<OauthToken> tokenResponse = restTemplate.postForEntity(tokenUri, httpEntity, OauthToken.class);
    if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null)
      throw new ApiReqException("Error getting access token", HttpStatus.BAD_GATEWAY.value());
    return tokenResponse.getBody();
  }

  public OauthToken refreshToken(String refreshToken) {
    URI tokenUri = URI.create(SwiftyConstants.API_42_TOKEN_URL);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set("Accept", "application/json");
    LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "refresh_token");
    form.add("client_id", configProperties.clientId());
    form.add("client_secret", configProperties.clientSecret());
    form.add("refresh_token", refreshToken);
    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(form, headers);
    // execute request
    ResponseEntity<OauthToken> tokenResponse = restTemplate.postForEntity(tokenUri, httpEntity, OauthToken.class);
    if (!tokenResponse.getStatusCode().is2xxSuccessful() || tokenResponse.getBody() == null)
      throw new ApiReqException("Error refreshing token", HttpStatus.BAD_GATEWAY.value());
    System.out.println("Token response body: " + tokenResponse.getBody());
    return tokenResponse.getBody();
  }

  public boolean isTokenExpired(OauthTokenInfo tokenInfo) {
    long currentTime = System.currentTimeMillis() / 1000L;
    return currentTime >= tokenInfo.expiryTime();
  }
}
