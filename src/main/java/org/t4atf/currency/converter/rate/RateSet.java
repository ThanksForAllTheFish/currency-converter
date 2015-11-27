package org.t4atf.currency.converter.rate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString(of = "rates")
@EqualsAndHashCode(of = "rates")
public class RateSet {

	private static final Currency DEFAULT = Currency.getInstance("EUR");
	private final Map<CurrencyMapping, FixedScaledRate> rates = new HashMap<>();
	private final Function<CurrencyMapping, Optional<FixedScaledRate>> retriever =
		(key) -> Optional.ofNullable(rates.get(key));
	private BiFunction<CurrencyMapping, Function<FixedScaledRate, FixedScaledRate>, Optional<BigDecimal>> rateCalculation =
		((BiFunction<CurrencyMapping, Function<FixedScaledRate, FixedScaledRate>, Optional<FixedScaledRate>>) (key, func) ->
			retriever.apply(key).map(func)).andThen((rate) -> rate.map((v) -> v.toRawValue()));

	public void add(Currency from, Currency to, BigDecimal ratio)
	{
		rates.put(new CurrencyMapping(from, to), amount(ratio));
	}

	public int size() {
		return rates.size();
	}

	public Optional<BigDecimal> convert(Currency from, Currency to, BigDecimal amount) {

		Optional<BigDecimal> direct = rateCalculation.apply(
			new CurrencyMapping(from, to), (rate) -> rate.multiply(amount(amount)));

		Optional<BigDecimal> inverse = rateCalculation.apply(
			new CurrencyMapping(to, from), (rate) -> amount(amount).divide(rate));

		return direct.isPresent() ? direct :
			inverse.isPresent() ? inverse :
				computeUnknownBaseRate(from, to, amount);
	}

	private Optional<BigDecimal> computeUnknownBaseRate(Currency from, Currency to, BigDecimal amount) {
		BiFunction<CurrencyMapping, CurrencyMapping, Optional<BigDecimal>> second =
			(key1, key2) ->
				retriever.apply(key1).flatMap(
					(val1) -> rateCalculation.apply(key2, (val2) -> val1.divide(val2).multiply(amount(amount)))
				);

		return second.apply(new CurrencyMapping(DEFAULT, to), new CurrencyMapping(DEFAULT, from));
	}

	private FixedScaledRate amount(BigDecimal amount) {
		return new FixedScaledRate(amount);
	}

	@Data
	private static class CurrencyMapping {
		private final Currency from;
		private final Currency to;
	}

	@ToString
	@EqualsAndHashCode
	private static class FixedScaledRate {

		private static final int SCALE = 6;
		private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

		private static final FixedScaledRate ONE = new FixedScaledRate(BigDecimal.ONE);

		private final BigDecimal rate;

		public FixedScaledRate(@NonNull BigDecimal rate) {
			this.rate = rate.setScale(SCALE, ROUNDING_MODE);
		}

		public FixedScaledRate multiply(FixedScaledRate amount) {
			return new FixedScaledRate(rate.multiply(amount.rate));
		}

		public FixedScaledRate divide(FixedScaledRate amount) {
			return new FixedScaledRate(rate.divide(amount.rate, ROUNDING_MODE));
		}

		public BigDecimal toRawValue() {
			return rate;
		}
	}
}
