package com.sw.tse.domain.expection;

public abstract class RegraDeNegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public RegraDeNegocioException(String message) {
        super(message);
    }
}

