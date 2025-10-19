package com.sw.tse.domain.expection;

public abstract class DadoInvalidoException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public DadoInvalidoException(String message) {
        super(message);
    }
}

