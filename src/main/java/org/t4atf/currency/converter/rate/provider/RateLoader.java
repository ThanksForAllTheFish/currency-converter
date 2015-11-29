package org.t4atf.currency.converter.rate.provider;

import java.util.Calendar;
import java.util.Optional;
import java.util.function.Predicate;

import org.t4atf.currency.converter.exceptions.RateProviderException;
import org.t4atf.currency.converter.rate.Rates;

import com.google.common.cache.CacheLoader;

/*
 * No need to synchronize shared status since RateLoader is used only in a single threaded context (see AppConfiguration).
 */
public class RateLoader extends CacheLoader<Object, Object> {
	private final static int MAX_FAILURE = 90;
	private final static Predicate<Calendar> DEFAULT_CHECKER = (date) -> {
		Calendar instance = new Calendar.Builder().setInstant(date.getTime()).build();
		instance.add(Calendar.DAY_OF_YEAR, MAX_FAILURE);
		return instance.compareTo(date) >= 0;
	};

	private final RateProvider rateProvider;

	private Optional<Calendar> lastSuccessfulLoadDate = Optional.empty();
	private Rates lastKnownRates = null;
	private Predicate<Calendar> lastUpdateChecker;

	public RateLoader(RateProvider rateProvider) {
		this(rateProvider, DEFAULT_CHECKER);
	}

	public RateLoader(RateProvider rateProvider, Predicate<Calendar> lastUpdateChecker) {
		this.rateProvider = rateProvider;
		this.lastUpdateChecker = lastUpdateChecker;
	}

	@Override
	public Object load(Object key) throws Exception {
		try {
			lastKnownRates = rateProvider.getRates();
			lastSuccessfulLoadDate = Optional.of(Calendar.getInstance());
			return lastKnownRates;
		} catch (RateProviderException ex) {
			if(lastKnownRates != null && lastSuccessfulLoadDate.filter(lastUpdateChecker).isPresent() ) {
				return lastKnownRates;
			}
			throw ex;
		}
	}
}
