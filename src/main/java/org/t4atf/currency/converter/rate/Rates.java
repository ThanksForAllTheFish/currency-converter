package org.t4atf.currency.converter.rate;

import java.util.Currency;
import java.util.Optional;

public interface Rates {

	Optional<FixedScaledRate> getRate(Currency from, Currency to);
}
