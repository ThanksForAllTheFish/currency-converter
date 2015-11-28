package org.t4atf.currency.converter.controllers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.t4atf.currency.converter.exceptions.UnknownCurrenciesException;
import org.t4atf.currency.converter.rate.FixedScaledRate;
import org.t4atf.currency.converter.rate.RateSet;
import org.t4atf.currency.converter.rate.provider.RateProvider;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
public class CurrencyConverter {

	private static final int AMOUNT_SCALE = 2;
	private static final int RATE_SCALE = 4;

	@Resource private RateProvider rateProvider;

	private BiFunction<FixedScaledRate, Integer, String> round = (value, scale) -> value.roundAt(scale).toString();

	@RequestMapping(value = "rate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Response getConversionRate(
			@RequestParam("from") Currency from,
			@RequestParam("to") Currency to,
			@RequestParam("amount") BigDecimal amount) {

		RateSet rates = rateProvider.getRates();
		Optional<FixedScaledRate> optionalRate = rates.getRate(from, to);
		return optionalRate.map(
			(rate) -> new Response(from.getCurrencyCode(), to.getCurrencyCode(), round.apply(new FixedScaledRate(amount), AMOUNT_SCALE),
				round.apply(rate, RATE_SCALE), round.apply(convertAt(rate, amount), AMOUNT_SCALE)))
			.orElseThrow( () -> new UnknownCurrenciesException(from, to));
	}

	private FixedScaledRate convertAt(FixedScaledRate rate, BigDecimal amount) {
		return rate.multiply(new FixedScaledRate(amount));
	}

	@RequiredArgsConstructor
	private static class Response {
		@JsonProperty("from")
		private final String from;
		@JsonProperty("to")
		private final String to;
		@JsonProperty("amount")
		private final String amount;
		@JsonProperty("rate")
		private final String rate;
		@JsonProperty("result")
		private final String result;
	}
}
