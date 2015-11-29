package org.t4atf.currency.converter.configuration;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.t4atf.currency.converter.utils.FileUtils.readFileAsString;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.t4atf.currency.converter.rate.provider.ECBRateProvider;

@Configuration
@Profile("test")
public class TestSpecificConfiguration {

	@Bean
	public RestTemplate restTemplate() {
		return new TestRestTemplate();
	}

	@Bean
	public MockRestServiceServer server(RestTemplate restTemplate) throws IOException, URISyntaxException {
		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer
			.expect(requestTo(ECBRateProvider.RATES_LOCATION)).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(readFileAsString("fakeRates.xml"), MediaType.TEXT_XML));
		return mockServer;
	}
}
