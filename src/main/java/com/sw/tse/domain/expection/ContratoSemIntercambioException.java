package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class ContratoSemIntercambioException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    private final Long idContrato;
    private final String numeroContrato;
    
    public ContratoSemIntercambioException(Long idContrato, String numeroContrato) {
        super(String.format("O Contrato %s ainda não tem associação com a RCI", numeroContrato));
        this.idContrato = idContrato;
        this.numeroContrato = numeroContrato;
    }
}

