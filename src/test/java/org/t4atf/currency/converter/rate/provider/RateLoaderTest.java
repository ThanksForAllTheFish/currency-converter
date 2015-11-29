package org.t4atf.currency.converter.rate.provider;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.function.Predicate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.t4atf.currency.converter.exceptions.RateProviderException;
import org.t4atf.currency.converter.rate.RateSet;

public class RateLoaderTest {

	@Rule public MockitoRule rule = MockitoJUnit.rule();
	@Rule public ExpectedException exception = ExpectedException.none();

	@Mock
	private RateProvider rateProvider;

	@Mock
	private Predicate<Calendar> checker;

	private RateLoader loader;

	@Test
	public void successfulLoad() throws Exception {
		loader = new RateLoader(rateProvider);

		when(rateProvider.getRates()).thenReturn(new RateSet());

		Object load = loader.load(new Object());

		assertThat(load, equalTo(new RateSet()));
	}

	@Test
	public void rateProviderFails() throws Exception {
		loader = new RateLoader(rateProvider);

		when(rateProvider.getRates()).thenThrow(new RateProviderException(new RuntimeException()));

		exception.expect(RateProviderException.class);
		loader.load(new Object());
	}

	@Test
	public void rateProviderFailsAtSecondCallButKnownRatesAreStillValid() throws Exception {
		loader = new RateLoader(rateProvider);

		when(rateProvider.getRates()).thenReturn(new RateSet());

		Object load = loader.load(new Object());

		when(rateProvider.getRates()).thenThrow(new RateProviderException(new RuntimeException()));

		loader.load(new Object());

		assertThat(load, equalTo(new RateSet()));
	}

	@Test
	public void rateProviderFailsAtSecondCallAndLastUpdateIsTooOld() throws Exception {
		loader = new RateLoader(rateProvider, checker);

		when(rateProvider.getRates()).thenReturn(new RateSet());

		loader.load(new Object());

		when(rateProvider.getRates()).thenThrow(new RateProviderException(new RuntimeException()));
		when(checker.test(argThat(any(Calendar.class)))).thenReturn(false);

		exception.expect(RateProviderException.class);
		loader.load(new Object());
	}
}