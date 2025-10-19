package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class OperadorSistemaNaoEncontradoException extends RecursoNaoEncontradoException {
    private static final long serialVersionUID = 1L;
    
    private final Long idPessoa;
    
    public OperadorSistemaNaoEncontradoException(Long idPessoa) {
        super(String.format("Operador sistema não encontrado para a pessoa ID %d", idPessoa));
        this.idPessoa = idPessoa;
    }
}

