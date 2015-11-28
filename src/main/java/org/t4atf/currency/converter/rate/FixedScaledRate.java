package org.t4atf.currency.converter.rate;

import java.math.BigDecimal;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class FixedScaledRate {

	private static final int CALCULATION_SCALE = 6;
	private static final int ROUNDING_MODE = BigDecimal.ROUND_HALF_UP;

	public static final FixedScaledRate ONE = new FixedScaledRate(BigDecimal.ONE);

	private final BigDecimal rate;

	public FixedScaledRate(@NonNull BigDecimal rate) {
		this.rate = rate.setScale(CALCULATION_SCALE, ROUNDING_MODE);
	}

	public FixedScaledRate multiply(FixedScaledRate amount) {
		return new FixedScaledRate(rate.multiply(amount.rate));
	}

	public FixedScaledRate divide(FixedScaledRate amount) {
		return new FixedScaledRate(rate.divide(amount.rate, ROUNDING_MODE));
	}

	public BigDecimal roundAt(int scale) {
		return rate.setScale(scale, ROUNDING_MODE);
	}
}