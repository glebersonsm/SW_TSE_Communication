package com.sw.tse.domain.expection;

public class PessoaSemContratoTseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PessoaSemContratoTseException(String message, Throwable cause) {
		super(message, cause);
	}

	public PessoaSemContratoTseException(String message) {
		super(message);
	}

}
