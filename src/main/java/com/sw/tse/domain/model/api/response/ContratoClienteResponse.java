package com.sw.tse.domain.model.api.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ContratoClienteResponse(
    Long idContrato,
    String numeroContrato,
    String cpfCessionario,
    String nomeCessionario,
    String cpfCocessionario,
    String nomeCocessionario,
    String status,
    BigDecimal valorNegociado,
    BigDecimal valorTotalEntrada,
    BigDecimal valorTotalIntegralizado,
    BigDecimal valorTotalEmAtraso,
    BigDecimal saldoDevedor,
    BigDecimal qtdPontosDisponiveis,
    BigDecimal qtdPontosUtilizados,
    BigDecimal qtdPontosDebitadosPorNaoUtilizacao,
    BigDecimal qtdTotalPontosDebitadosPorNaoUtilizacao,
    Integer qtdParcelasVencidas,
    Integer qtdDiasVencido,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime dataVenda,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime dataEfetivacaoDebitoPorNaoUtilizacao,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime dataEfetivarDebitoPorNaoUtilizacao,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime proximaUtilizacao,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime dataInicioVigencia,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    OffsetDateTime dataFimVigencia,
    Long idGrupoTabelaPontos,
    Long idEmpresa
) {
}