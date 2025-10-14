package com.sw.tse.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "sw.tse.periodo")
@Getter
@Setter
public class PeriodoUtilizacaoParametros {

    /**
     * Período mínimo de antecedência para reserva em dias
     */
    private Integer antecedenciaMinimaDias = 2;

    /**
     * Dias mínimos para RCI (Registro de Contrato de Intercâmbio)
     */
    private Integer rciDiasMinimos = 120;

    /**
     * Dia limite para pool (formato DD)
     */
    private Integer poolDiaLimite = 30;

    /**
     * Mês limite para pool (formato MM)
     */
    private Integer poolMesLimite = 6;

    /**
     * ID da intercambiadora RCI
     */
    private Long intercambiadoraRciId = 1L;
}
