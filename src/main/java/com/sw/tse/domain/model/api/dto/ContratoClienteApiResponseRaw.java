package com.sw.tse.domain.model.api.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContratoClienteApiResponseRaw {
    
    private Long idContrato;
    private String numeroContrato;
    private String descricaoProduto;
    private BigDecimal qtdPontosContratados;
    private BigDecimal percentualMinimoIntegralizadoExigido;
    private BigDecimal qtdPontosDebitados;
    private BigDecimal qtdPontosUtilizados;
    private BigDecimal qtdPontosCompraAvulsa;
    private BigDecimal qtdPontosDebitarPorNaoUtilizacao;
    private BigDecimal qtdPontosDebitadosPorNaoUtilizacao;
    private BigDecimal qtdTotalPontosDebitadosPorNaoUtilizacao;
    private Timestamp dataVenda;
    private Timestamp dataEfetivacaoDebitoPorNaoUtilizacao;
    private Timestamp dataEfetivarDebitoPorNaoUtilizacao;
    private BigDecimal valorNegociado;
    private BigDecimal valorTotalEntrada;
    private BigDecimal valorTotalSaldoRestante;
    private BigDecimal porcentagemIntegralizadaSobreValorNegociado;
    private String nomeCessionario;
    private BigDecimal qtdPontosLiberadosParaUso;
    private Integer qtdParcelasVencidas;
    private Integer qtdDiasVencido;
    private Timestamp proximaUtilizacao;
    private Long idGrupoTabelaPontos;
    private Long idEmpresa;
    private Timestamp dataInicioVigencia;
    private Timestamp dataFimVigencia;
    private BigDecimal valorTotalIntegralizado;
    private BigDecimal valorTotalEmAtraso;
    private BigDecimal porcentagemAIntegralizarSobreValorNegociado;
    private String tipoContrato;
    private String statusContrato;
    private BigDecimal saldoPontosGeral;
    private BigDecimal valorBrutoRecebido;
    private BigDecimal valorReembolsoPago;
    private Long idContratoOrigemAdm;
    private String siglaEmpresa;
}