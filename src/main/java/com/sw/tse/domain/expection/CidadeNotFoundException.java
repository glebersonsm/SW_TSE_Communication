package com.sw.tse.domain.expection;

public class CidadeNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CidadeNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CidadeNotFoundException(String message) {
		super(message);
	}

}
