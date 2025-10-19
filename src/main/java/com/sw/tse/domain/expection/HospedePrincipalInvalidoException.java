package com.sw.tse.domain.expection;

public class HospedePrincipalInvalidoException extends DadoInvalidoException {
    private static final long serialVersionUID = 1L;
    
    public HospedePrincipalInvalidoException(String mensagem) {
        super(mensagem);
    }
}

