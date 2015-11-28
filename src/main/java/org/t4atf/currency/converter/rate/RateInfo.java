package org.t4atf.currency.converter.rate;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class RateInfo {

	private final BigDecimal rate;
	private final BigDecimal result;
}
