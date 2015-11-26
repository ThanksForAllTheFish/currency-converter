package org.t4atf.currency.converter.controllers;

import java.math.BigDecimal;
import java.util.Currency;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.t4atf.currency.converter.rate.provider.RateProvider;

@RestController
@RequestMapping("/")
public class CurrencyConverter {

	@Resource private RateProvider rateProvider;

	@RequestMapping(value = "rate", method = RequestMethod.GET)
	public BigDecimal getConversionRate(
			@RequestParam("from") Currency source,
			@RequestParam("to") Currency target) {

		return BigDecimal.valueOf(rateProvider.getRates().size());
	}
}
