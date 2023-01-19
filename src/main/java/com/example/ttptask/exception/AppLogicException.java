package com.example.ttptask.exception;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class AppLogicException extends RuntimeException {

	private final ErrorCode errorCode;
	private final String[] args;

	public AppLogicException(ErrorCode errorCode, Object... args) {
		this.errorCode = errorCode;
		this.args = Arrays.stream(args).map(String::valueOf).toArray(String[]::new);
	}

	public AppLogicException(ErrorCode errorCode, Throwable cause, Object... args) {
		super(cause);
		this.errorCode = errorCode;
		this.args = Arrays.stream(args).map(String::valueOf).toArray(String[]::new);
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public String[] getArgs() {
		return args == null ? null : Arrays.copyOf(args, args.length);
	}
}
