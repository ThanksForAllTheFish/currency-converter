package org.t4atf.currency.converter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.t4atf.currency.converter.utils.FileUtils.readFileAsString;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.t4atf.currency.converter.rate.provider.ECBRateProvider;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@SpringApplicationConfiguration(SpringBootApplication.class)
@WebIntegrationTest({"server.port=9000"})
@ActiveProfiles("test")
public class SpringBootApplicationTest {

	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	@Resource
	private RestTemplate restTemplate;

	private RestTemplate client;
	private MockRestServiceServer mockServer;

	@Before
	public void init() throws IOException, URISyntaxException {
		client = new TestRestTemplate();
		client.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		mockServer = MockRestServiceServer.createServer(restTemplate);
		mockServer
			.expect(requestTo(ECBRateProvider.RATES_LOCATION)).andExpect(method(HttpMethod.GET))
			.andRespond(withSuccess(readFileAsString("fakeRates.xml"), MediaType.TEXT_XML));
	}

	@Test
	public void validConversion() {
		Response rate = client.getForObject("http://localhost:9000/rate?from=EUR&to=USD&amount=100", Response.class);
		assertThat(rate, equalTo(new Response("EUR", "USD", "100", "106.12")));
	}

	@Test
	public void unknownCurrencyCombination() {
		ResponseEntity<String> response = client.getForEntity("http://localhost:9000/rate?from=EUR&to=GBP&amount=100", String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}

	@After
	public void close() {
	    mockServer.verify();
	}

	@ToString
	@EqualsAndHashCode
	private static class Response {
		private final String from;
		private final String to;
		private final String amount;
		private final String result;

		private Response(String from, String to, String amount, String result) {
			this.from = from;
			this.to = to;
			this.amount = amount;
			this.result = result;
		}

		@JsonCreator
		public static Response newResponse(@JsonProperty("from") String from,
										   @JsonProperty("to") String to,
										   @JsonProperty("amount") String amount,
										   @JsonProperty("result") String result) {
			return new Response(from, to, amount, result);
		}
	}
}