package com.sw.tse.domain.expection;

public class FaixaEtariaNotFoundException extends RuntimeException {
    
    public FaixaEtariaNotFoundException(String sigla) {
        super(String.format("Faixa etária com sigla '%s' não encontrada", sigla));
    }
}

