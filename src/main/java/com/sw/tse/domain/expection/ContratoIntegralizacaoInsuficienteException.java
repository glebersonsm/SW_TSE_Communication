package com.sw.tse.domain.expection;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class ContratoIntegralizacaoInsuficienteException extends RuntimeException {
    
    private final Long idContrato;
    private final String numeroContrato;
    private final BigDecimal valorIntegralizado;
    private final BigDecimal valorMinimoExigido;
    private final BigDecimal diferencaFaltante;
    
    public ContratoIntegralizacaoInsuficienteException(Long idContrato, String numeroContrato,
            BigDecimal valorIntegralizado, BigDecimal valorMinimoExigido) {
        super(String.format("Contrato: %s tem integralização insuficiente: R$ %.2f (mínimo: R$ %.2f)", 
            numeroContrato, valorIntegralizado, valorMinimoExigido));
        this.idContrato = idContrato;
        this.numeroContrato = numeroContrato;
        this.valorIntegralizado = valorIntegralizado;
        this.valorMinimoExigido = valorMinimoExigido;
        this.diferencaFaltante = valorMinimoExigido.subtract(valorIntegralizado);
    }
}
