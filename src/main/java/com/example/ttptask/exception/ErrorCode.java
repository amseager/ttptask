package com.example.ttptask.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	INVALID_REQUEST(HttpStatus.BAD_REQUEST),

	DUPLICATE_CLIENT(HttpStatus.BAD_REQUEST),
	CLIENT_NOT_FOUND(HttpStatus.BAD_REQUEST),

	DUPLICATE_CONVERSION(HttpStatus.BAD_REQUEST),
	CONVERSION_AMOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST),

	EXTERNAL_SERVICE_ERROR(HttpStatus.FAILED_DEPENDENCY),

	UNHANDLED(HttpStatus.INTERNAL_SERVER_ERROR);

	private final HttpStatus httpStatus;
}
