package org.t4atf.currency.converter.rate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import org.junit.Test;

public class RateSetTest {

	private static final RateSet RATE_SET = fakeRakeSet();

	@Test
	public void convertFromEURtoX() {
		assertThat(RATE_SET.convert(Currency.getInstance("EUR"), Currency.getInstance("USD"), new BigDecimal("100")),
			equalTo(Optional.of(newBigDecimal("150.0"))));
		assertThat(RATE_SET.convert(Currency.getInstance("EUR"), Currency.getInstance("JPY"), new BigDecimal(".5")),
			equalTo(Optional.of(newBigDecimal("75.0"))));
	}

	@Test
	public void convertFromXtoEUR() {
		assertThat(RATE_SET.convert(Currency.getInstance("USD"), Currency.getInstance("EUR"), new BigDecimal("15")),
			equalTo(Optional.of(newBigDecimal("10.0"))));
		assertThat(RATE_SET.convert(Currency.getInstance("JPY"), Currency.getInstance("EUR"), new BigDecimal("150")),
			equalTo(Optional.of(newBigDecimal("1.0"))));
	}

	@Test
	public void convertFromXtoX() {
		assertThat(RATE_SET.convert(Currency.getInstance("USD"), Currency.getInstance("JPY"), new BigDecimal("1")),
			equalTo(Optional.of(newBigDecimal("100.0"))));
	}

	@Test
	public void unknownCombination() {
		assertThat(RATE_SET.convert(Currency.getInstance("USD"), Currency.getInstance("GBP"), new BigDecimal("15")),
			equalTo(Optional.empty()));
		assertThat(RATE_SET.convert(Currency.getInstance("GBP"), Currency.getInstance("USD"), new BigDecimal("15")),
			equalTo(Optional.empty()));
	}

	private BigDecimal newBigDecimal(String value) {
		return new BigDecimal(value).setScale(6);
	}

	private static RateSet fakeRakeSet() {
		RateSet rateSet = new RateSet();
		rateSet.add(Currency.getInstance("EUR"), Currency.getInstance("USD"), new BigDecimal("1.5"));
		rateSet.add(Currency.getInstance("EUR"), Currency.getInstance("JPY"), new BigDecimal("150"));
		return rateSet;
	}
}