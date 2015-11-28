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
import lombok.ToString;

@ToString(of = "rates")
@EqualsAndHashCode(of = "rates")
public class RateSet {

	private static final Currency DEFAULT = Currency.getInstance("EUR");
	private final Map<CurrencyMapping, FixedScaledRate> rates = new HashMap<>();
	private final Function<CurrencyMapping, Optional<FixedScaledRate>> retriever =
		(key) -> Optional.ofNullable(rates.get(key));
	private BiFunction<CurrencyMapping, Function<FixedScaledRate, FixedScaledRate>, Optional<FixedScaledRate>> rateCalculation =
		(key, func) -> retriever.apply(key).map(func);

	public void add(Currency from, Currency to, BigDecimal ratio)
	{
		rates.put(new CurrencyMapping(from, to), amount(ratio));
	}

	public Optional<FixedScaledRate> getRate(Currency from, Currency to) {

		Optional<FixedScaledRate> direct = rateCalculation.apply(
			new CurrencyMapping(from, to), Function.identity());

		Optional<FixedScaledRate> inverse = rateCalculation.apply(
			new CurrencyMapping(to, from), (rate) -> FixedScaledRate.ONE.divide(rate));

		return direct.isPresent() ? direct :
			inverse.isPresent() ? inverse :
				computeUnknownBaseRate(from, to);
	}

	private Optional<FixedScaledRate> computeUnknownBaseRate(Currency from, Currency to) {
		BiFunction<CurrencyMapping, CurrencyMapping, Optional<FixedScaledRate>> second =
			  (keyFrom, keyTo) ->
				retriever.apply(keyFrom).flatMap(
					(rateFrom) -> rateCalculation.apply(keyTo, (rateTo) -> rateFrom.divide(rateTo))
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
}
