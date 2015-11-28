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

	private final Currency from;
	private final Currency to;
	private final Optional<FixedScaledRate> expected;

	@Parameterized.Parameters(name = "{index}: rate between {1} and {2} should be {3} ")
	public static List<Object[]> data() {
		return Arrays.asList(
			new Object[]{Currency.getInstance("EUR"), Currency.getInstance("USD"), expectedValue("1.5")},
			new Object[]{Currency.getInstance("EUR"), Currency.getInstance("JPY"), expectedValue("150")},
			new Object[]{Currency.getInstance("USD"), Currency.getInstance("EUR"), expectedValue("0.666667")},
			new Object[]{Currency.getInstance("JPY"), Currency.getInstance("EUR"), expectedValue("0.006667")},
			new Object[]{Currency.getInstance("JPY"), Currency.getInstance("USD"), expectedValue("0.01")},
			new Object[]{Currency.getInstance("USD"), Currency.getInstance("GBP"), Optional.empty()},
			new Object[]{Currency.getInstance("GBP"), Currency.getInstance("JPY"), Optional.empty()}
		);
	}

	@Test
	public void test() {
		assertThat(RATE_SET.getRate(from, to), equalTo(expected));
	}

	private static Optional<FixedScaledRate> expectedValue(String value) {
		return
			Optional.of(new FixedScaledRate(new BigDecimal(value)));
	}

	private static RateSet fakeRakeSet() {
		RateSet rateSet = new RateSet();
		rateSet.add(Currency.getInstance("EUR"), Currency.getInstance("USD"), new BigDecimal("1.5"));
		rateSet.add(Currency.getInstance("EUR"), Currency.getInstance("JPY"), new BigDecimal("150"));
		return rateSet;
	}
}