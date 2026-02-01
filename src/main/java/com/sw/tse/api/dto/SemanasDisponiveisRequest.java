package com.sw.tse.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SemanasDisponiveisRequest {

    @NotNull(message = "ID do contrato é obrigatório")
    private Long idcontrato;

    private Integer ano;

    /**
     * Tipo de validação de integralização enviado pela Portal API (config INTEGRALIZACAO_CONTRATO_CONFIG).
     * Valores: "FIXO" ou "PERCENTUAL". Se null, a validação de integralização será pulada.
     */
    private String tipoValidacaoIntegralizacao;

    /**
     * Valor de integralização enviado pela Portal API.
     * FIXO: valor mínimo em reais; PERCENTUAL: percentual (ex: 12 para 12%).
     */
    private BigDecimal valorIntegralizacao;
}
