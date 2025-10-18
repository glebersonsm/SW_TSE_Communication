package com.sw.tse.domain.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InadimplenciaDto {
    
    /**
     * Quantidade de parcelas inadimplentes
     */
    private Long quantidadeParcelas;
    
    /**
     * Valor total inadimplente
     */
    private BigDecimal valorInadimplente;
    
    /**
     * Tipo de inadimplência
     */
    private TipoInadimplencia tipo;
    
    /**
     * Construtor para uso em JPQL com String
     */
    public InadimplenciaDto(Long quantidadeParcelas, BigDecimal valorInadimplente, String tipoString) {
        this.quantidadeParcelas = quantidadeParcelas;
        this.valorInadimplente = valorInadimplente;
        this.tipo = TipoInadimplencia.valueOf(tipoString);
    }
    
    public enum TipoInadimplencia {
        CONTRATO,
        CONDOMINIO
    }
}
