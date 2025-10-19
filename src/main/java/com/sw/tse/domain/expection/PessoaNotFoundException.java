package com.sw.tse.domain.expection;

public class PessoaNotFoundException extends RecursoNaoEncontradoException {
    private static final long serialVersionUID = 1L;
    
    public PessoaNotFoundException(Long idPessoa) {
        super(String.format("Pessoa com ID %d não encontrada", idPessoa));
    }
}

