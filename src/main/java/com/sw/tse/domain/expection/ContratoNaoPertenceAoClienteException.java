package com.sw.tse.domain.expection;

public class ContratoNaoPertenceAoClienteException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;

    public ContratoNaoPertenceAoClienteException(String message) {
        super(message);
    }
}
