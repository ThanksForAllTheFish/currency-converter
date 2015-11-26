package org.t4atf.currency.converter.rate;

import java.util.HashSet;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RateSet {

	private final Set<Rate> rates = new HashSet<>();

	public void add(Rate rate) {
		rates.add(rate);
	}

	public int size() {
		return rates.size();
	}
}
