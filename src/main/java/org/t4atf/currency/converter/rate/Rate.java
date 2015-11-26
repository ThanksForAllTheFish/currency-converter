package org.t4atf.currency.converter.rate;

import java.math.BigDecimal;
import java.util.Currency;

import lombok.Data;

@Data(staticConstructor = "of")
public class Rate {

	private final Currency from;
	private final Currency to;
	private final BigDecimal ratio;
}
