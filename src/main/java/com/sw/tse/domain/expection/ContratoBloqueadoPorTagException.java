package com.sw.tse.domain.expection;

import lombok.Getter;

@Getter
public class ContratoBloqueadoPorTagException extends RuntimeException {
    
    private final Long idContrato;
    private final String numeroContrato;
    private final String descricaoTag;
    private final String tipoBloqueio;
    
    public ContratoBloqueadoPorTagException(Long idContrato, String numeroContrato, 
            String descricaoTag, String tipoBloqueio) {
        super(String.format("Contrato: %s possui %s. Entre em contato com a central de relacionamento para mais informações.", 
            numeroContrato, descricaoTag));
        this.idContrato = idContrato;
        this.numeroContrato = numeroContrato;
        this.descricaoTag = descricaoTag;
        this.tipoBloqueio = tipoBloqueio;
    }
}
