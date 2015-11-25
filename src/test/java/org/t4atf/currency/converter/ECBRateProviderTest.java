package org.t4atf.currency.converter;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class ECBRateProviderTest {

	private ECBRateProvider provider = new ECBRateProvider(new RestTemplate());

	@Test
	public void getRates() {
		Set<Rate> rates = provider.getRates();

		assertThat(rates, containsInAnyOrder(new Rate(Currency.getInstance("EUR"), Currency.getInstance("USD"), BigDecimal.ONE)));
	}
}