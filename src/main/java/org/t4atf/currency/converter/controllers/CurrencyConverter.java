package org.t4atf.currency.converter.controllers;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.t4atf.currency.converter.exceptions.UnknownCurrenciesException;
import org.t4atf.currency.converter.rate.RateSet;
import org.t4atf.currency.converter.rate.provider.RateProvider;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
public class CurrencyConverter {

	@Resource private RateProvider rateProvider;

	@RequestMapping(value = "rate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Response getConversionRate(
			@RequestParam("from") Currency from,
			@RequestParam("to") Currency to,
			@RequestParam("amount") BigDecimal amount) {

		RateSet rates = rateProvider.getRates();
		Optional<BigDecimal> converted = rates.convert(from, to, amount);
		return converted.map( (result) -> new Response(from.getCurrencyCode(), to.getCurrencyCode(), amount.toString(), result.toString()) )
			.orElseThrow( () -> new UnknownCurrenciesException(from, to));
	}

	@RequiredArgsConstructor
	private static class Response {
		@JsonProperty("from")
		private final String from;
		@JsonProperty("to")
		private final String to;
		@JsonProperty("amount")
		private final String amount;
		@JsonProperty("result")
		private final String result;
	}
}
