package com.sw.tse.domain.model.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContratoClienteApiResponse(
    @JsonProperty("IdContrato") Long idContrato,
    @JsonProperty("NumeroContrato") String numeroContrato,
    @JsonProperty("DescricaoProduto") String descricaoProduto,
    @JsonProperty("QtdPontosContratados") BigDecimal qtdPontosContratados,
    @JsonProperty("PercentualMinimoIntegralizadoExigido") BigDecimal percentualMinimoIntegralizadoExigido,
    @JsonProperty("QtdPontosDebitados") BigDecimal qtdPontosDebitados,
    @JsonProperty("QtdPontosUtilizados") BigDecimal qtdPontosUtilizados,
    @JsonProperty("QtdPontosCompraAvulsa") BigDecimal qtdPontosCompraAvulsa,
    @JsonProperty("QtdPontosDebitarPorNaoUtilizacao") BigDecimal qtdPontosDebitarPorNaoUtilizacao,
    @JsonProperty("QtdPontosDebitadosPorNaoUtilizacao") BigDecimal qtdPontosDebitadosPorNaoUtilizacao,
    @JsonProperty("QtdTotalPontosDebitadosPorNaoUtilizacao") BigDecimal qtdTotalPontosDebitadosPorNaoUtilizacao,
    @JsonProperty("DataVenda") OffsetDateTime dataVenda,
    @JsonProperty("DataEfetivacaoDebitoPorNaoutilizacao") OffsetDateTime dataEfetivacaoDebitoPorNaoUtilizacao,
    @JsonProperty("DataEfetivarDebitoPorNaoutilizacao") OffsetDateTime dataEfetivarDebitoPorNaoUtilizacao,
    @JsonProperty("ValorNegociado") BigDecimal valorNegociado,
    @JsonProperty("ValorTotalEntrada") BigDecimal valorTotalEntrada,
    @JsonProperty("ValorTotalSaldoRestante") BigDecimal valorTotalSaldoRestante,
    @JsonProperty("PorcentagemIntegralizadaSobreValorNegociado") BigDecimal porcentagemIntegralizadaSobreValorNegociado,
    @JsonProperty("NomeCessionario") String nomeCessionario,
    @JsonProperty("QtdPontosLiberadosParaUso") BigDecimal qtdPontosLiberadosParaUso,
    @JsonProperty("QtdParcelasVencidas") Integer qtdParcelasVencidas,
    @JsonProperty("QtdDiasVencido") Integer qtdDiasVencido,
    @JsonProperty("ProximaUtilizacao") OffsetDateTime proximaUtilizacao,
    @JsonProperty("IdGrupoTabelaPontos") Long idGrupoTabelaPontos,
    @JsonProperty("IdEmpresa") Long idEmpresa,
    @JsonProperty("DataInicioVigencia") OffsetDateTime dataInicioVigencia,
    @JsonProperty("DataFimVigencia") OffsetDateTime dataFimVigencia,
    @JsonProperty("ValorTotalIntegralizado") BigDecimal valorTotalIntegralizado,
    @JsonProperty("ValorTotalEmAtraso") BigDecimal valorTotalEmAtraso,
    @JsonProperty("PorcentagemAIntegralizarSobreValorNegociado") BigDecimal porcentagemAIntegralizarSobreValorNegociado,
    @JsonProperty("TipoContrato") String tipoContrato,
    @JsonProperty("StatusContrato") String statusContrato,
    @JsonProperty("SaldoPontosGeral") BigDecimal saldoPontosGeral,
    @JsonProperty("ValorBrutoRecebido") BigDecimal valorBrutoRecebido,
    @JsonProperty("ValorReembolsoPago") BigDecimal valorReembolsoPago
) {}