package com.sw.tse.domain.expection;

public abstract class DadoObrigatorioException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public DadoObrigatorioException(String message) {
        super(message);
    }
}

