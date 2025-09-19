package com.sw.tse.domain.expection;

public class ClienteNaoEncontratoTseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClienteNaoEncontratoTseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClienteNaoEncontratoTseException(String message) {
		super(message);
	}

}
