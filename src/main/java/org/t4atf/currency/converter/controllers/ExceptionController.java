package org.t4atf.currency.converter.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.t4atf.currency.converter.exceptions.UnknownCurrenciesException;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
public class ExceptionController {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionController.class);

	@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Unknown currency mapping")
	@ExceptionHandler
	public ErrorInfo notFound(HttpServletRequest request, UnknownCurrenciesException ex) {
		LOG.warn(ex.getMessage(), ex);
		return new ErrorInfo(request.getRequestURL().toString(), ex.getMessage());
	}

	@RequiredArgsConstructor
	private static class ErrorInfo {
		private final String url;
		private final String message;
	}
}
