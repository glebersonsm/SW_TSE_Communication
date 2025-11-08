package com.sw.tse.domain.model.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContratoClienteApiResponse {
    
    @JsonProperty("IdContrato")
    private Long idContrato;
    
    @JsonProperty("NumeroContrato")
    private String numeroContrato;
    
    @JsonProperty("DescricaoProduto")
    private String descricaoProduto;
    
    @JsonProperty("QtdPontosContratados")
    private BigDecimal qtdPontosContratados;
    
    @JsonProperty("PercentualMinimoIntegralizadoExigido")
    private BigDecimal percentualMinimoIntegralizadoExigido;
    
    @JsonProperty("QtdPontosDebitados")
    private BigDecimal qtdPontosDebitados;
    
    @JsonProperty("QtdPontosUtilizados")
    private BigDecimal qtdPontosUtilizados;
    
    @JsonProperty("QtdPontosCompraAvulsa")
    private BigDecimal qtdPontosCompraAvulsa;
    
    @JsonProperty("QtdPontosDebitarPorNaoUtilizacao")
    private BigDecimal qtdPontosDebitarPorNaoUtilizacao;
    
    @JsonProperty("QtdPontosDebitadosPorNaoUtilizacao")
    private BigDecimal qtdPontosDebitadosPorNaoUtilizacao;
    
    @JsonProperty("QtdTotalPontosDebitadosPorNaoUtilizacao")
    private BigDecimal qtdTotalPontosDebitadosPorNaoUtilizacao;
    
    @JsonProperty("DataVenda")
    private OffsetDateTime dataVenda;
    
    @JsonProperty("DataEfetivacaoDebitoPorNaoutilizacao")
    private OffsetDateTime dataEfetivacaoDebitoPorNaoUtilizacao;
    
    @JsonProperty("DataEfetivarDebitoPorNaoutilizacao")
    private OffsetDateTime dataEfetivarDebitoPorNaoUtilizacao;
    
    @JsonProperty("ValorNegociado")
    private BigDecimal valorNegociado;
    
    @JsonProperty("ValorTotalEntrada")
    private BigDecimal valorTotalEntrada;
    
    @JsonProperty("ValorTotalSaldoRestante")
    private BigDecimal valorTotalSaldoRestante;
    
    @JsonProperty("PorcentagemIntegralizadaSobreValorNegociado")
    private BigDecimal porcentagemIntegralizadaSobreValorNegociado;
    
    @JsonProperty("NomeCessionario")
    private String nomeCessionario;
    
    @JsonProperty("QtdPontosLiberadosParaUso")
    private BigDecimal qtdPontosLiberadosParaUso;
    
    @JsonProperty("QtdParcelasVencidas")
    private Integer qtdParcelasVencidas;
    
    @JsonProperty("QtdDiasVencido")
    private Integer qtdDiasVencido;
    
    @JsonProperty("ProximaUtilizacao")
    private OffsetDateTime proximaUtilizacao;
    
    @JsonProperty("IdGrupoTabelaPontos")
    private Long idGrupoTabelaPontos;
    
    @JsonProperty("IdEmpresa")
    private Long idEmpresa;
    
    @JsonProperty("DataInicioVigencia")
    private OffsetDateTime dataInicioVigencia;
    
    @JsonProperty("DataFimVigencia")
    private OffsetDateTime dataFimVigencia;
    
    @JsonProperty("ValorTotalIntegralizado")
    private BigDecimal valorTotalIntegralizado;
    
    @JsonProperty("ValorTotalEmAtraso")
    private BigDecimal valorTotalEmAtraso;
    
    @JsonProperty("PorcentagemAIntegralizarSobreValorNegociado")
    private BigDecimal porcentagemAIntegralizarSobreValorNegociado;
    
    @JsonProperty("TipoContrato")
    private String tipoContrato;
    
    @JsonProperty("StatusContrato")
    private String statusContrato;
    
    @JsonProperty("SaldoPontosGeral")
    private BigDecimal saldoPontosGeral;
    
    @JsonProperty("ValorBrutoRecebido")
    private BigDecimal valorBrutoRecebido;
    
    @JsonProperty("ValorReembolsoPago")
    private BigDecimal valorReembolsoPago;

    @JsonProperty("IdContratoOrigemAdm")
    private Long idContratoOrigemAdm;

    @JsonProperty("EmpresaSigla")
    private String siglaEmpresa;
}