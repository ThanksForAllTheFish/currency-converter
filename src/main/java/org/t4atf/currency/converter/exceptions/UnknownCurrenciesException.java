package org.t4atf.currency.converter.exceptions;

import java.util.Currency;

public class UnknownCurrenciesException extends RuntimeException {

	public UnknownCurrenciesException(Currency from, Currency to) {
		super("Unknown currency combination " + from + "/" + to);
	}
}
