package com.sw.tse.domain.expection;

public class TipoUtilizacaoContratoInvalidoException extends DadoInvalidoException {
    private static final long serialVersionUID = 1L;
    
    public TipoUtilizacaoContratoInvalidoException(String sigla) {
        super(String.format("TipoUtilizacaoContrato deve ter sigla 'RESERVA', mas foi informado '%s'", sigla));
    }
}

