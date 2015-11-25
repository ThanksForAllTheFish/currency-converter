package org.t4atf.currency.converter;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.web.client.RestTemplate;

@SpringApplicationConfiguration(SpringBootApplication.class)
@WebIntegrationTest({"server.port=9000"})
public class SpringBootApplicationTest {

	@ClassRule
	public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	RestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void start() {
		BigDecimal rate = restTemplate.getForObject("http://localhost:9000/rate?from=EUR&to=USD", BigDecimal.class);
		assertThat(rate, equalTo(BigDecimal.TEN));
	}
}