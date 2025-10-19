package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class PeriodoNaoPermitePoolException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    private final Long idPeriodoUtilizacao;
    private final String descricaoPeriodo;
    
    public PeriodoNaoPermitePoolException(Long idPeriodoUtilizacao, String descricaoPeriodo) {
        super(String.format("O período '%s' não atende os requisitos para POOL. " +
                "Verifique se o período está dentro do prazo permitido para POOL.", descricaoPeriodo));
        this.idPeriodoUtilizacao = idPeriodoUtilizacao;
        this.descricaoPeriodo = descricaoPeriodo;
    }
}

