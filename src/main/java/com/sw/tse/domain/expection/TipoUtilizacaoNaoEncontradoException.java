package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class TipoUtilizacaoNaoEncontradoException extends RuntimeException {
    
    private final String sigla;
    
    public TipoUtilizacaoNaoEncontradoException(String sigla) {
        super(String.format("Tipo de utilização '%s' não encontrado", sigla));
        this.sigla = sigla;
    }
}

