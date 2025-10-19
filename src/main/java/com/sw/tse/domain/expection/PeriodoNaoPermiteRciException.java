package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class PeriodoNaoPermiteRciException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    private final Long idPeriodoUtilizacao;
    private final String descricaoPeriodo;
    
    public PeriodoNaoPermiteRciException(Long idPeriodoUtilizacao, String descricaoPeriodo) {
        super(String.format("O período '%s' não atende os requisitos mínimos para reserva RCI. " +
                "Verifique a antecedência mínima necessária.", descricaoPeriodo));
        this.idPeriodoUtilizacao = idPeriodoUtilizacao;
        this.descricaoPeriodo = descricaoPeriodo;
    }
}

