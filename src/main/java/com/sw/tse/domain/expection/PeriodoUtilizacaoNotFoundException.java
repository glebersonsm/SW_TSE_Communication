package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class PeriodoUtilizacaoNotFoundException extends RecursoNaoEncontradoException {
    private static final long serialVersionUID = 1L;
    
    private final Long idPeriodoUtilizacao;
    
    public PeriodoUtilizacaoNotFoundException(Long idPeriodoUtilizacao) {
        super(String.format("Período de utilização ID %d não encontrado", idPeriodoUtilizacao));
        this.idPeriodoUtilizacao = idPeriodoUtilizacao;
    }
}

