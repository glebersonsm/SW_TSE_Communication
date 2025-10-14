package com.sw.tse.domain.expection;

public class ContratoNaoPertenceAoClienteException extends RuntimeException {

    public ContratoNaoPertenceAoClienteException(String message) {
        super(message);
    }

    public ContratoNaoPertenceAoClienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
