package com.sw.tse.domain.expection;

public class TokenJwtInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenJwtInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenJwtInvalidoException(String message) {
        super(message);
    }
}
