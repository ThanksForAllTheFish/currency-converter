package org.t4atf.currency.converter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.t4atf.currency.converter.rate.provider.RateProvider;

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
	private MockRestServiceServer server;

	@Resource
	private RateProvider rateProvider;

	private static RestTemplate CLIENT;

	@BeforeClass
	public static void setup() throws IOException, URISyntaxException {
		CLIENT = new TestRestTemplate();
		CLIENT.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	}

	@Before
	public void preLoadRates() {
		rateProvider.getRates();
	}

	@Test
	public void validConversion() {
		Response rate = CLIENT.getForObject("http://localhost:9000/rate?from=EUR&to=USD&amount=100", Response.class);
		assertThat(rate, equalTo(new Response("EUR", "USD", "100.00", "1.0612", "106.12")));
	}

	@Test
	public void missingRequiredParameter() {
		ResponseEntity<String> response = CLIENT.getForEntity("http://localhost:9000/rate?from=EUR&to=USD", String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));;
	}

	@Test
	public void invalidAmount() {
		ResponseEntity<String> response = CLIENT.getForEntity("http://localhost:9000/rate?from=EUR&to=USD&amount=z", String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));;
	}

	@Test
	public void invalidCurrency() {
		ResponseEntity<String> response = CLIENT.getForEntity("http://localhost:9000/rate?from=EUR&to=US&amount=100", String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));;
	}

	@Test
	public void unknownCurrencyCombination() {
		ResponseEntity<String> response = CLIENT.getForEntity("http://localhost:9000/rate?from=EUR&to=GBP&amount=100", String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}

	@After
	public void close() {
		server.verify();
	}

	@ToString
	@EqualsAndHashCode
	private static class Response {
		private final String from;
		private final String to;
		private final String amount;
		private final String rate;
		private final String result;

		private Response(String from, String to, String amount, String rate, String result) {
			this.from = from;
			this.to = to;
			this.amount = amount;
			this.rate = rate;
			this.result = result;
		}

		@JsonCreator
		public static Response newResponse(@JsonProperty("from") String from,
										   @JsonProperty("to") String to,
										   @JsonProperty("amount") String amount,
										   @JsonProperty("rate") String rate,
										   @JsonProperty("result") String result) {
			return new Response(from, to, amount, rate, result);
		}
	}
}