package org.t4atf.currency.converter.configuration;

import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("test")
public class TestSpecificConfiguration {

	@Bean
	public RestTemplate restTemplate() {
		return new TestRestTemplate();
	}
}
