package org.t4atf.currency.converter.configuration;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.util.Arrays;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.t4atf.currency.converter.controllers.ExceptionController;
import org.t4atf.currency.converter.rate.provider.ECBRateProvider;
import org.t4atf.currency.converter.rate.provider.RateLoader;
import org.t4atf.currency.converter.rate.provider.RateProvider;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

@Configuration
public class AppConfiguration {

	@Bean
	public RateProvider rateProvider(RestTemplate restTemplate) {
		return new ECBRateProvider(restTemplate);
	}

	@Bean
	public ExceptionController exceptionController() {
		return new ExceptionController();
	}

	@Bean
	public CacheLoader<Object, Object> ratesCacheLoader(RateProvider rateProvider) {
		return new RateLoader(rateProvider);
	}

	@Bean
	public CacheManager cacheManager(CacheLoader<Object, Object> ratesCacheLoader) {
		GuavaCacheManager cacheManager = new GuavaCacheManager();

		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("CacheRefresher-pool-%d").setDaemon(true).build();

		GuavaCache cache = new GuavaCache("ECBRates", CacheBuilder.newBuilder()
			.refreshAfterWrite(1, TimeUnit.DAYS)
			.recordStats()
			.build(
				CacheLoader.asyncReloading(ratesCacheLoader, MoreExecutors.listeningDecorator(newSingleThreadExecutor(threadFactory)))));

		cacheManager.setCacheNames(Arrays.asList(cache.getName()));

		return cacheManager;
	}
}
