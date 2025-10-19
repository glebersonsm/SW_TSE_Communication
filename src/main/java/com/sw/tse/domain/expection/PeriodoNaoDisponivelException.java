package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class PeriodoNaoDisponivelException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    private final Long idContrato;
    private final Long idPeriodoUtilizacao;
    
    public PeriodoNaoDisponivelException(Long idContrato, Long idPeriodoUtilizacao) {
        super("O período selecionado não está mais disponível para reserva");
        this.idContrato = idContrato;
        this.idPeriodoUtilizacao = idPeriodoUtilizacao;
    }
}

