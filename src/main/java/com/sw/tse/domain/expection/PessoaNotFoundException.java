package com.sw.tse.domain.expection;

public class PessoaNotFoundException extends RuntimeException {
    
    public PessoaNotFoundException(Long idPessoa) {
        super(String.format("Pessoa com ID %d não encontrada", idPessoa));
    }
}

