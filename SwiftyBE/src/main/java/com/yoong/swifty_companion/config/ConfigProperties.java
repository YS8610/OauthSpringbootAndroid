package com.yoong.swifty_companion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record ConfigProperties(String clientId, String clientSecret) {

}
