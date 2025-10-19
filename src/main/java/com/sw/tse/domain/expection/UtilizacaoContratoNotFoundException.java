package com.sw.tse.domain.expection;

public class UtilizacaoContratoNotFoundException extends RecursoNaoEncontradoException {
    
    private static final long serialVersionUID = 1L;
    
    public UtilizacaoContratoNotFoundException(Long idUtilizacaoContrato) {
        super(String.format("Utilizacao de contrato com ID %d nao encontrada", idUtilizacaoContrato));
    }
}

