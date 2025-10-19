package com.sw.tse.domain.expection;

public class CancelamentoNaoPermitidoException extends RegraDeNegocioException {
    
    private static final long serialVersionUID = 1L;
    
    public CancelamentoNaoPermitidoException(String tipoUtilizacao) {
        super(String.format("Cancelamento de utilizacao tipo %s nao esta permitido", tipoUtilizacao));
    }
}

