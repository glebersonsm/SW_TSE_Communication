package com.sw.tse.domain.expection;

public class LoginInvalidoTseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LoginInvalidoTseException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoginInvalidoTseException(String message) {
		super(message);
	}

}
