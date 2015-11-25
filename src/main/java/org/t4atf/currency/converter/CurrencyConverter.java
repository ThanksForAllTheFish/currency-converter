package org.t4atf.currency.converter;

import java.math.BigDecimal;
import java.util.Currency;

import javax.xml.stream.XMLStreamException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CurrencyConverter {
	@RequestMapping(value = "rate", method = RequestMethod.GET)
	public BigDecimal getConversionRate(
			@RequestParam("from") Currency source,
			@RequestParam("to") Currency target) throws XMLStreamException {

		return BigDecimal.TEN;
	}
}
