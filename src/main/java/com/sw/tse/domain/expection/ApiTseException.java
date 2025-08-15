package com.sw.tse.domain.expection;

public class ApiTseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApiTseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiTseException(String message) {
		super(message);
	}

}
