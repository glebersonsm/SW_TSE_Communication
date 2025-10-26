package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class UtilizacaoContratoNaoEditavelException extends RegraDeNegocioException {
    private static final long serialVersionUID = 1L;
    
    private final Long idUtilizacaoContrato;
    private final String tipoUtilizacao;
    
    public UtilizacaoContratoNaoEditavelException(Long idUtilizacaoContrato, String tipoUtilizacao) {
        super(String.format("A utilização de contrato ID %d (tipo: %s) não pode ser editada. " +
                "Apenas utilizações do tipo RESERVA podem ser editadas", 
                idUtilizacaoContrato, tipoUtilizacao));
        this.idUtilizacaoContrato = idUtilizacaoContrato;
        this.tipoUtilizacao = tipoUtilizacao;
    }
}
