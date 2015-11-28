package org.t4atf.currency.converter.rate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import lombok.RequiredArgsConstructor;

@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class RateSetTest {

	private static final RateSet RATE_SET = fakeRakeSet();

	private final BigDecimal amount;
	private final Currency from;
	private final Currency to;
	private final Optional<BigDecimal> expected;

	@Parameterized.Parameters(name = "{index}: converting {0}{1} to {2} should return {3} ")
	public static List<Object[]> data() {
		return Arrays.asList(
			new Object[]{new BigDecimal("100"), Currency.getInstance("EUR"), Currency.getInstance("USD"), newBigDecimal("150")},
			new Object[]{new BigDecimal(".5"), Currency.getInstance("EUR"), Currency.getInstance("JPY"), newBigDecimal("75")},
			new Object[]{new BigDecimal("15"), Currency.getInstance("USD"), Currency.getInstance("EUR"), newBigDecimal("10")},
			new Object[]{new BigDecimal("150"), Currency.getInstance("JPY"), Currency.getInstance("EUR"), newBigDecimal("1")},
			new Object[]{new BigDecimal("0.5"), Currency.getInstance("JPY"), Currency.getInstance("USD"), newBigDecimal("0.01")},
			new Object[]{new BigDecimal("1"), Currency.getInstance("USD"), Currency.getInstance("GBP"), Optional.empty()},
			new Object[]{new BigDecimal("1"), Currency.getInstance("GBP"), Currency.getInstance("JPY"), Optional.empty()}
		);
	}

	@Test
	public void test() {
		assertThat(RATE_SET.convert(from, to, amount), equalTo(expected));
	}

	private static Optional<BigDecimal> newBigDecimal(String value) {
		return
			Optional.of(new BigDecimal(value).setScale(2));
	}

	private static RateSet fakeRakeSet() {
		RateSet rateSet = new RateSet();
		rateSet.add(Currency.getInstance("EUR"), Currency.getInstance("USD"), new BigDecimal("1.5"));
		rateSet.add(Currency.getInstance("EUR"), Currency.getInstance("JPY"), new BigDecimal("150"));
		return rateSet;
	}
}