package com.sw.tse.domain.expection;

public class PagamentoCartaoException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    public PagamentoCartaoException(String message) {
        super(message);
    }
    
    public PagamentoCartaoException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}

