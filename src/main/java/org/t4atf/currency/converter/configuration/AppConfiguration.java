package org.t4atf.currency.converter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.t4atf.currency.converter.rate.provider.ECBRateProvider;
import org.t4atf.currency.converter.rate.provider.RateProvider;

@Configuration
public class AppConfiguration {

	@Bean
	public RateProvider rateProvider(RestTemplate restTemplate) {
		return new ECBRateProvider(restTemplate);
	}
}
