package org.t4atf.currency.converter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ECBRateProvider {
	private static Logger LOG = LoggerFactory.getLogger(CurrencyConverter.class);

	private final RestTemplate restTemplate;

	public ECBRateProvider(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Set<Rate> getRates() {
		LOG.info("Called");
		Set<Rate> rateSet = new HashSet<>();
		try {
			XMLReader saxReader = null;
			saxReader = XMLReaderFactory.createXMLReader();
			DefaultHandler handler = new DefaultHandler() {
				public void startElement(String uri, String localName,
										 String qName, Attributes attributes) {
					if (localName.equals("Cube")) {
						String currency = attributes.getValue("currency");
						String rate = attributes.getValue("rate");
						if (currency != null && rate != null) {
							rateSet.add(new Rate(Currency.getInstance("EUR"), Currency.getInstance(currency),
								new BigDecimal(rate)));
						}
					}
				}
			};
			saxReader.setContentHandler(handler);
			saxReader.setErrorHandler(handler);
			saxReader.parse("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		return rateSet;
	}
}
