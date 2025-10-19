package com.sw.tse.domain.expection;

public class UtilizacaoContratoCanceladaException extends RegraDeNegocioException {
    
    private static final long serialVersionUID = 1L;
    
    public UtilizacaoContratoCanceladaException(Long idUtilizacaoContrato) {
        super(String.format("Utilizacao de contrato %d ja esta cancelada", idUtilizacaoContrato));
    }
}

