package com.sw.tse.domain.expection;

public class SexoObrigatorioException extends DadoObrigatorioException {
    private static final long serialVersionUID = 1L;
    
    public SexoObrigatorioException() {
        super("Sexo é obrigatório");
    }
}

