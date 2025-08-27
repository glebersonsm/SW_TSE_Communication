package com.sw.tse.domain.expection;

public class ValorPadraoNaoConfiguradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ValorPadraoNaoConfiguradoException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValorPadraoNaoConfiguradoException(String message) {
		super(message);
	}

}
