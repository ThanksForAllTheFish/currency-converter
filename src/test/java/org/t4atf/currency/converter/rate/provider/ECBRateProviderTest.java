package org.t4atf.currency.converter.rate.provider;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.t4atf.currency.converter.utils.FileUtils.readFileAsString;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.web.client.RestTemplate;
import org.t4atf.currency.converter.exceptions.RateProviderException;
import org.t4atf.currency.converter.rate.Rate;
import org.t4atf.currency.converter.rate.RateSet;

public class ECBRateProviderTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	private final RestTemplate client = new RestTemplate();
	private final MockRestServiceServer server = MockRestServiceServer.createServer(client);
	private final ResponseActions serverActions = server.expect(requestTo(ECBRateProvider.RATES_LOCATION)).andExpect(method(HttpMethod.GET));
	private final ECBRateProvider provider = new ECBRateProvider(client);

	@Test
	public void getRates() throws ParseException, IOException, URISyntaxException {
		serverActions.andRespond(withSuccess(readFileAsString("fakeRates.xml"), MediaType.TEXT_XML));
		RateSet rates = provider.getRates();

		RateSet expected = new RateSet();
		expected.add(Rate.of(Currency.getInstance("EUR"), Currency.getInstance("USD"), rate("1.0612")));
		expected.add(Rate.of(Currency.getInstance("EUR"), Currency.getInstance("JPY"), rate("130.06")));

		assertThat(rates, equalTo(expected));
	}

	@Test
	public void handleServerError() throws IOException, URISyntaxException {
		serverActions.andRespond(withSuccess(readFileAsString("malformed.xml"), MediaType.TEXT_XML));

		exception.expect(RateProviderException.class);

		provider.getRates();
	}

	@Test
	public void handleUnparsableRate() throws IOException, URISyntaxException {
		serverActions.andRespond(withSuccess(readFileAsString("unparsableRate.xml"), MediaType.TEXT_XML));

		exception.expect(RateProviderException.class);

		provider.getRates();
	}

	private BigDecimal rate(String rate) throws ParseException {
		return new BigDecimal(rate);
	}
}