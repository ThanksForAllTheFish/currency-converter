package org.t4atf.currency.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@EnableCaching
@ComponentScan(basePackages = {
	"org.t4atf.currency.converter.controllers",
	"org.t4atf.currency.converter.configuration"
})
public class SpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApplication.class, args);
	}
}
