package com.sw.tse.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaFinanceiraClienteDto {

    @JsonProperty("Id")
    private Long id;

    @JsonProperty("BoletoId")
    private Long boletoId;

    @JsonProperty("Contrato")
    private Long contrato;

    @JsonProperty("StatusParcela")
    private String statusParcela;

    @JsonProperty("DataHoraCriacao")
    private LocalDateTime dataHoraCriacao;

    @JsonProperty("EmpresaId")
    private Long empresaId;

    @JsonProperty("EmpresaNome")
    private String empresaNome;

    @JsonProperty("PessoaProviderId")
    private Long pessoaProviderId;

    @JsonProperty("PessoaId")
    private Long pessoaId;

    @JsonProperty("ClienteId")
    private Long clienteId;

    @JsonProperty("NomePessoa")
    private String nomePessoa;

    @JsonProperty("DataCriacao")
    private LocalDateTime dataCriacao;

    @JsonProperty("Vencimento")
    private LocalDateTime vencimento;

    @JsonProperty("CodigoTipoConta")
    private Integer codigoTipoConta;

    @JsonProperty("NomeTipoConta")
    private String nomeTipoConta;

    @JsonProperty("Valor")
    private BigDecimal valor;

    @JsonProperty("Saldo")
    private BigDecimal saldo;

    @JsonProperty("LinhaDigitavelBoleto")
    private String linhaDigitavelBoleto;

    @JsonProperty("NossoNumeroBoleto")
    private String nossoNumeroBoleto;

    @JsonProperty("Observacao")
    private String observacao;

    @JsonProperty("EmpreendimentoCnpj")
    private String empreendimentoCnpj;

    @JsonProperty("EmpreendimentoNome")
    private String empreendimentoNome;

    @JsonProperty("PessoaEmpreendimentoId")
    private Long pessoaEmpreendimentoId;

    @JsonProperty("NumeroImovel")
    private String numeroImovel;

    @JsonProperty("NumeroContrato")
    private String numeroContrato;

    @JsonProperty("DocumentoCliente")
    private String documentoCliente;

    @JsonProperty("FracaoCota")
    private String fracaoCota;

    @JsonProperty("IdTorre")
    private Long idTorre;

    @JsonProperty("BlocoCodigo")
    private String blocoCodigo;

    @JsonProperty("LimitePagamentoTransmitido")
    private LocalDateTime limitePagamentoTransmitido;

    @JsonProperty("ComLimitePagamentoTra")
    private String comLimitePagamentoTra;

    @JsonProperty("ComLimitePagamento")
    private String comLimitePagamento;

    @JsonProperty("ValorJuroDiario")
    private BigDecimal valorJuroDiario;

    @JsonProperty("PercentualJuroDiario")
    private BigDecimal percentualJuroDiario;

    @JsonProperty("PercentualJuroDiarioCar")
    private BigDecimal percentualJuroDiarioCar;

    @JsonProperty("PercentualJuroMensal")
    private BigDecimal percentualJuroMensal;

    @JsonProperty("ValorJuroMensal")
    private BigDecimal valorJuroMensal;

    @JsonProperty("PercentualMulta")
    private BigDecimal percentualMulta;

    @JsonProperty("PercentualMultaCar")
    private BigDecimal percentualMultaCar;

    @JsonProperty("ValorAtualizado")
    private BigDecimal valorAtualizado;

    @JsonProperty("TaxaJuroMensalProcessamento")
    private BigDecimal taxaJuroMensalProcessamento;

    @JsonProperty("TaxaMultaMensalProcessamento")
    private BigDecimal taxaMultaMensalProcessamento;

    @JsonProperty("DataBaseAplicacaoJurosMultas")
    private LocalDateTime dataBaseAplicacaoJurosMultas;

    @JsonProperty("PodeAplicarMulta")
    private String podeAplicarMulta;

    @JsonProperty("DataHoraBaixa")
    private LocalDateTime dataHoraBaixa;

    @JsonProperty("StatusCrcBloqueiaPagamento")
    private String statusCrcBloqueiaPagamento;

    @JsonProperty("DataProcessamentoCartaoRec")
    private LocalDateTime dataProcessamentoCartaoRec;

    @JsonProperty("DataProcessamento")
    private LocalDateTime dataProcessamento;

    @JsonProperty("IdMeioPagamento")
    private Long idMeioPagamento;

    @JsonProperty("MeioPagamento")
    private String meioPagamento;

    @JsonProperty("Juros")
    private BigDecimal juros; // Valor de juros calculado para esta conta

    @JsonProperty("Multa")
    private BigDecimal multa; // Valor de multa calculada para esta conta

    @JsonProperty("MemoriaCalculo")
    private String memoriaCalculo; // Memória de cálculo de juros e multas (apenas no modo simulação)

    @JsonProperty("QuantidadeDiasAtraso")
    private Integer quantidadeDiasAtraso; // Quantidade de dias de atraso
}
