package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class ContratoIntercambioNotFoundException extends RuntimeException {
    
    private final Long idContratoIntercambio;
    
    public ContratoIntercambioNotFoundException(Long idContratoIntercambio) {
        super(String.format("Contrato de intercâmbio ID %d não encontrado", idContratoIntercambio));
        this.idContratoIntercambio = idContratoIntercambio;
    }
}

