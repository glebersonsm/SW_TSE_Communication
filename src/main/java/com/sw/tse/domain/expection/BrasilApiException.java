package com.sw.tse.domain.expection;

public class BrasilApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BrasilApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public BrasilApiException(String message) {
		super(message);
	}

}
