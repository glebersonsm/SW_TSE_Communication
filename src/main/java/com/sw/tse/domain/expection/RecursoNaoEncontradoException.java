package com.sw.tse.domain.expection;

public abstract class RecursoNaoEncontradoException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}

