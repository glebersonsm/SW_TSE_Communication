package com.sw.tse.domain.expection;

public class TipoUtilizacaoContratoNullException extends RuntimeException {
    
    public TipoUtilizacaoContratoNullException() {
        super("Tipo de utilização contrato não pode ser nulo");
    }
}
