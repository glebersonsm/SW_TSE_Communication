package com.sw.tse.domain.expection;

import java.math.BigDecimal;

import com.sw.tse.domain.model.dto.InadimplenciaDto.TipoInadimplencia;

import lombok.Getter;

@Getter
public class ContratoInadimplenteException extends RuntimeException {
    
    private final Long idContrato;
    private final String numeroContrato;
    private final TipoInadimplencia tipoInadimplencia;
    private final Long quantidadeParcelas;
    private final BigDecimal valorInadimplente;
    
    public ContratoInadimplenteException(Long idContrato, String numeroContrato, 
            TipoInadimplencia tipoInadimplencia, Long quantidadeParcelas, BigDecimal valorInadimplente) {
        super(String.format("Contrato: %s está inadimplente (%s): %d parcelas, valor R$ %.2f", 
            numeroContrato, tipoInadimplencia.name(), quantidadeParcelas, valorInadimplente));
        this.idContrato = idContrato;
        this.numeroContrato = numeroContrato;
        this.tipoInadimplencia = tipoInadimplencia;
        this.quantidadeParcelas = quantidadeParcelas;
        this.valorInadimplente = valorInadimplente;
    }
}
