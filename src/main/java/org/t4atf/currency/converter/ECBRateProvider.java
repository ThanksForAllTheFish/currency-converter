package org.t4atf.currency.converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.t4atf.currency.converter.exceptions.RateProviderException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ECBRateProvider {
	public static final String RATES_LOCATION = "http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
	public static final DecimalFormat DECIMAL_FORMAT = buildDecimalFormat();
	private static Logger LOG = LoggerFactory.getLogger(CurrencyConverter.class);

	private final RestTemplate restTemplate;

	public ECBRateProvider(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Set<Rate> getRates() {
		return handle((set) -> buildRates(set), new HashSet<Rate>());
	}

	private Set<Rate> buildRates(Set<Rate> rateAccumulator) throws SAXException, IOException, ParserConfigurationException {
		DefaultHandler handler = new DefaultHandler() {
			public void startElement(String uri, String localName,
									 String qName, Attributes attributes) {
				if (qName.equals("Cube")) {
					UnsafeOperation<Attributes, Optional<Rate>> op = (attrs) -> {
						String currency = attrs.getValue("currency");
						String rate = attrs.getValue("rate");
						if (currency != null && rate != null) {
							return Optional.of(Rate.of(Currency.getInstance("EUR"), Currency.getInstance(currency), numericalRate(rate)));
						}
						return Optional.empty();
					};

					handle(op, attributes).ifPresent((r) -> rateAccumulator.add(r));
				}
			}

			private BigDecimal numericalRate(String rate) throws ParseException {
				return (BigDecimal) DECIMAL_FORMAT.parse(rate);
			}
		};

		SAXParserFactory.newInstance().newSAXParser().parse(retrieveRawRates(), handler);
		return rateAccumulator;
	}

	private <I, O> O handle(UnsafeOperation<I, O> op, I input) {
		try {
			return op.tryTo(input);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new RateProviderException(e);
		}
	}

	private InputStream retrieveRawRates() throws UnsupportedEncodingException {
		String response = restTemplate.getForObject(RATES_LOCATION, String.class);
		return new ByteArrayInputStream(response.getBytes("UTF-8"));
	}

	private static DecimalFormat buildDecimalFormat() {
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setParseBigDecimal(true);
		return decimalFormat;
	}

	private interface UnsafeOperation<I, O> {
		O tryTo(I input) throws Exception;
	}
}
