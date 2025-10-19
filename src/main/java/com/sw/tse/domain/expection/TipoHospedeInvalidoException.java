package com.sw.tse.domain.expection;

public class TipoHospedeInvalidoException extends DadoInvalidoException {
    private static final long serialVersionUID = 1L;
    
    public TipoHospedeInvalidoException(String tipoInformado) {
        super(String.format("Tipo de hóspede '%s' é inválido", tipoInformado));
    }
}

