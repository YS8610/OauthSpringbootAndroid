package com.yoong.swifty_companion;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.yoong.swifty_companion.config.ConfigProperties;
import com.yoong.swifty_companion.model.OauthTokenInfo;


@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class SwiftyCompanionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiftyCompanionApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public Map<String, OauthTokenInfo> inMemoryUserStore() {
		return new HashMap<>();
	}
}
