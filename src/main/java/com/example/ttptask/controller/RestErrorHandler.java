package com.example.ttptask.controller;

import com.example.ttptask.dto.ErrorResponseDto;
import com.example.ttptask.exception.AppLogicException;
import com.example.ttptask.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class RestErrorHandler extends ResponseEntityExceptionHandler {

	private static final String LOG_PREFIX_WARN = "Warn: ";
	private static final String LOG_PREFIX_ERROR = "Error: ";

	private final MessageSource messageSource;

	@ExceptionHandler(AppLogicException.class)
	public ResponseEntity<ErrorResponseDto> handleAppLogicException(final AppLogicException ex) {
		log.warn(LOG_PREFIX_WARN, ex);
		final ErrorCode errorCode = ex.getErrorCode();
		final Object[] args = ex.getArgs();

		return ResponseEntity.status(errorCode.getHttpStatus())
				.body(args == null ? buildErrorResponse(errorCode) : buildErrorResponse(errorCode, args));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
		return handleBadRequest(ex);

	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
	                                                              HttpHeaders headers,
	                                                              HttpStatus status,
	                                                              WebRequest request) {
		return handleBadRequest(ex);

	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	                                                              HttpHeaders headers,
	                                                              HttpStatus status,
	                                                              WebRequest request) {
		return handleBadRequest(ex);
	}

	private ResponseEntity<Object> handleBadRequest(Exception ex) {
		log.warn(LOG_PREFIX_WARN, ex);
		return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus())
				.body(buildErrorResponse(ErrorCode.INVALID_REQUEST, ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponseDto handleGeneralException(final Exception ex) {
		log.error(LOG_PREFIX_ERROR, ex);
		return buildErrorResponse(ErrorCode.UNHANDLED, ex.getMessage());
	}

	private String buildMessage(final String messageKey, final Object... args) {
		return messageSource.getMessage(messageKey, args, Locale.getDefault());
	}

	private ErrorResponseDto buildErrorResponse(final ErrorCode errorCode, final Object... args) {
		return new ErrorResponseDto().code(errorCode.name()).message(buildMessage(errorCode.name(), args));
	}
}
