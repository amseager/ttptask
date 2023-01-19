package com.example.ttptask.exception;

import lombok.Getter;
import org.springframework.web.client.RestClientException;

@Getter
public class ExternalCallRestClientException extends RestClientException {

	private final String error;

	public ExternalCallRestClientException(final String error, final String msg) {
		super(msg);
		this.error = error;
	}
}
