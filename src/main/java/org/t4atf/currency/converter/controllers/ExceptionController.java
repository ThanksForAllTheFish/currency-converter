package org.t4atf.currency.converter.controllers;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.t4atf.currency.converter.exceptions.RateProviderException;
import org.t4atf.currency.converter.exceptions.UnknownCurrenciesException;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
public class ExceptionController {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionController.class);

	@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Unknown currency mapping")
	@ExceptionHandler
	public ErrorInfo notFound(HttpServletRequest request, UnknownCurrenciesException ex) {
		return logAndReturnError(request, ex);
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Missing mandatory parameter")
	@ExceptionHandler
	public ErrorInfo badRequest(HttpServletRequest request, MissingServletRequestParameterException ex) {
		return logAndReturnError(request, ex);
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid query parameter value")
	@ExceptionHandler
	public ErrorInfo badRequest(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
		return logAndReturnError(request, ex);
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Fail to load rates from remote service")
	@ExceptionHandler
	public ErrorInfo knownInternalServerError(HttpServletRequest request, RateProviderException ex) {
		return logAndReturnError(request, ex);
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Unknown error")
	@ExceptionHandler
	public ErrorInfo internalServerError(HttpServletRequest request, Exception ex) {
		return logAndReturnError(request, ex);
	}

	private ErrorInfo logAndReturnError(HttpServletRequest request, Exception ex) {
		LOG.warn(ex.getMessage(), ex);
		return new ErrorInfo(request.getRequestURL().toString(), ex.getMessage());
	}

	@RequiredArgsConstructor
	private static class ErrorInfo {
		private final String url;
		private final String message;
	}
}
