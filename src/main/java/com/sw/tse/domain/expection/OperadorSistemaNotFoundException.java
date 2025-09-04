package com.sw.tse.domain.expection;

public class OperadorSistemaNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OperadorSistemaNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public OperadorSistemaNotFoundException(String message) {
		super(message);
	}

}
