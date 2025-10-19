package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class ContratoNotFoundException extends RecursoNaoEncontradoException {
    private static final long serialVersionUID = 1L;
    
    private final Long idContrato;
    
    public ContratoNotFoundException(Long idContrato) {
        super(String.format("Contrato ID %d não encontrado", idContrato));
        this.idContrato = idContrato;
    }
}

