package com.yoong.swiftycompanion.model;

public record OauthTokenDTO(
        String access_token,
        String token_type,
        int expires_in,
        String refresh_token,
        String scope,
        int created_at,
        int secret_valid_until) {}
