package org.t4atf.currency.converter.exceptions;

import java.util.Currency;

public class UnknownCurrenciesException extends RuntimeException {

	private static final long serialVersionUID = -5929402450149362516L;

	public UnknownCurrenciesException(Currency from, Currency to) {
		super("Unknown currency combination " + from + "/" + to);
	}
}
