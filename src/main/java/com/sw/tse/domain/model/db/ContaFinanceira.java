package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sw.tse.core.util.ParametroFinanceiroHelper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contafinanceira")
@Setter(value = AccessLevel.PRIVATE)
@Getter()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ContaFinanceira {

    // === ESSENCIAIS ===
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcontafinanceira")
    @SequenceGenerator(name = "seqcontafinanceira", sequenceName = "seqcontafinanceira", allocationSize = 1)
    @Column(name = "idcontafinanceira")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", insertable = true, updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao", insertable = false, updatable = true)
    private LocalDateTime dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcadastro", insertable = true, updatable = false)
    private OperadorSistema responsavelCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespalteracao", insertable = false, updatable = true)
    private OperadorSistema responsavelAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontrato")
    private Contrato contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpessoa")
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontamovimento")
    private ContaMovimentacaoBancaria contaMovimentacaoBancaria;

    @Column(name = "numerodocumento", length = 120)
    private String numeroDocumento;

    @Column(name = "datavencimento")
    private LocalDateTime dataVencimento;

    @Column(name = "datavencimentooriginal")
    private LocalDateTime dataVencimentoOriginal;

    @Column(name = "nroparcela")
    private Integer numeroParcela;

    @Column(name = "historico", length = 2550)
    private String historico;

    @Column(name = "tipohistorico", length = 30)
    private String tipoHistorico;

    @Column(name = "destinocontafinanceira", length = 20)
    private String destinoContaFinanceira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idorigemconta")
    private TipoOrigemContaFinanceira origemConta;

    @Column(name = "valorreceber", precision = 15, scale = 2)
    private BigDecimal valorReceber;

    @Column(name = "valorparcela", precision = 15, scale = 2)
    private BigDecimal valorParcela;

    @Column(name = "valorcapital", precision = 15, scale = 2)
    private BigDecimal valorCapital;

    @Column(name = "valorjuros", precision = 15, scale = 2)
    private BigDecimal valorJuros;

    @Column(name = "txjuros")
    private Double taxaJuros;

    @Column(name = "valorjurosfinanc", precision = 15, scale = 2)
    private BigDecimal valorJurosFinanc;

    @Column(name = "txjurosfinancmensal")
    private Double taxaJurosFinancMensal;

    @Column(name = "valormulta", precision = 15, scale = 2)
    private BigDecimal valorMulta;

    @Column(name = "valoracrescimo", precision = 15, scale = 2)
    private BigDecimal valorAcrescimo;

    @Column(name = "valordesconto", precision = 15, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "valordescontomanual", precision = 15, scale = 2)
    private BigDecimal valorDescontoManual;

    @Column(name = "valorrecebido", precision = 15, scale = 2)
    private BigDecimal valorRecebido;

    @Column(name = "valoracrescimoacumuladocorrecaomonetaria", precision = 15, scale = 2)
    private BigDecimal valorAcrescimoAcumuladoCorrecaoMonetaria;

    @Column(name = "pago")
    private Boolean pago;

    @Column(name = "datapagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "databaixa")
    private LocalDateTime dataBaixa;

    @Column(name = "historicobaixa", length = 250)
    private String historicoBaixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespbaixa")
    private OperadorSistema responsavelBaixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idmeiopagamento")
    private MeioPagamento meioPagamento;

    // === BOLETO ===
    @Column(name = "identificadorinternoboleto", length = 60)
    private String identificadorInternoBoleto;

    @Column(name = "linhadigitavelboleto", length = 120)
    private String linhaDigitavelBoleto;

    @Column(name = "codigobarrasboleto", length = 120)
    private String codigoBarrasBoleto;

    @Column(name = "nossonumeroboletocalculado", length = 60)
    private String nossoNumeroBoletoCalculado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcarteiraboleto")
    private CarteiraBoleto carteiraBoleto;

    @Column(name = "taxaboleto", precision = 19, scale = 5)
    private BigDecimal taxaBoleto;

    @Column(name = "boletoregistrado")
    private Boolean boletoRegistrado;

    @Column(name = "enviadoparacobranca")
    private Boolean enviadoParaCobranca;

    @Column(name = "dataenviocobranca")
    private LocalDateTime dataEnvioCobranca;

    @Column(name = "statuscobranca", length = 30)
    private String statusCobranca;

    @Column(name = "datageracaoremessa")
    private LocalDateTime dataGeracaoRemessa;

    @Column(name = "instrucaomanualboleto", length = 2048)
    private String instrucaoManualBoleto;

    @Column(name = "idbandeiracartao")
    private Long idBandeiraCartao;

    @Column(name = "numerocartao", length = 20)
    private String numeroCartao;

    @Column(name = "quatroultimosdigitoscartao", length = 4)
    private String quatroUltimosDigitosCartao;

    @Column(name = "qtdparcelascartao")
    private Integer qtdParcelasCartao;

    @Column(name = "codsegurancacartao")
    private Integer codSegurancaCartao;

    @Column(name = "nomeimpressocartao", length = 30)
    private String nomeImpressoCartao;

    @Column(name = "validadecartao", length = 5)
    private String validadeCartao;

    @Column(name = "taxacartao", precision = 19, scale = 5)
    private BigDecimal taxaCartao;

    @Column(name = "descontotaxacartao", precision = 15, scale = 2)
    private BigDecimal descontoTaxaCartao;

    @Column(name = "idcartaovinculadopessoa")
    private Long idCartaoVinculadoPessoa;

    @Column(name = "assinaturaemarquivocartao")
    private Boolean assinaturaEmArquivoCartao;

    @Column(name = "idbandeirasaceitas")
    private Long idBandeirasAceitas;

    @Column(name = "idtransacaocartaocreditodebito")
    private Long idTransacaoCartaoCreditoDebito;

    @Column(name = "qtdparcelasparatransacaocredito")
    private Integer qtdParcelasParaTransacaoCredito;

    @Column(name = "recorrenciaautorizada")
    private Boolean recorrenciaAutorizada;

    @Column(name = "datavencimentorecorrencia")
    private LocalDateTime dataVencimentoRecorrencia;

    // === PIX ===
    @Column(name = "pixcopiaecola", columnDefinition = "TEXT")
    private String pixCopiaECola;

    @Column(name = "pixqrcode", columnDefinition = "TEXT")
    private String pixQrCode;

    @Column(name = "txid", columnDefinition = "TEXT")
    private String txId;

    @Column(name = "datageracaopix")
    private LocalDateTime dataGeracaoPix;

    @Column(name = "pixrecorrente")
    private Boolean pixRecorrente;

    // === CANCELAMENTO E ESTORNO ===
    @Column(name = "datacancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "historicocancelamento", length = 250)
    private String historicoCancelamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcancelamento")
    private OperadorSistema responsavelCancelamento;

    @Column(name = "dataestorno")
    private LocalDateTime dataEstorno;

    @Column(name = "historicoestornobaixa", length = 1024)
    private String historicoEstornoBaixa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespestorno")
    private OperadorSistema responsavelEstorno;

    @Column(name = "dataliquidacao")
    private LocalDateTime dataLiquidacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontafinanceiraorigem")
    private ContaFinanceira contaFinanceiraOrigem;

    @Column(name = "guidmerchantorderid", columnDefinition = "TEXT")
    private String guidMerchantOrderId;

    @Column(name = "precobrancavenda")
    private Boolean preCobrancaVenda;
    

    public BigDecimal calcularValorTotal() {
        BigDecimal valorParcela = this.valorParcela != null ? this.valorParcela : BigDecimal.ZERO;
        BigDecimal valorCorrecao = this.valorAcrescimoAcumuladoCorrecaoMonetaria != null ? 
                this.valorAcrescimoAcumuladoCorrecaoMonetaria : BigDecimal.ZERO;
        BigDecimal valorAcrescimo = this.getValorAcrescimo() != null ? this.getValorAcrescimo() : BigDecimal.ZERO;
        BigDecimal valorDesconto = this.valorDesconto != null ? this.valorDesconto : BigDecimal.ZERO;
        BigDecimal valorDescontoManual = this.valorDescontoManual != null ? 
                this.valorDescontoManual : BigDecimal.ZERO;
        
        return valorParcela.add(valorCorrecao).add(valorAcrescimo).subtract(valorDesconto).subtract(valorDescontoManual);
    }
    

    public String calcularStatus() {

        if (Boolean.TRUE.equals(this.pago) || 
            "BAIXADO".equals(this.tipoHistorico) || 
            "TRANSFERIDO".equals(this.tipoHistorico) || 
            "BAIXADOCARTACREDITO".equals(this.tipoHistorico)) {
            return "PAGO";
        }

        if (this.meioPagamento != null && this.meioPagamento.getCodMeioPagamento() != null) {
            String codMeioPagamento = this.meioPagamento.getCodMeioPagamento().toUpperCase();
            
            if ("CARTAO".equals(codMeioPagamento) && 
                Boolean.FALSE.equals(this.meioPagamento.getUtilizadoParaLinkPagamento())) {
                return "PAGO";
            }
            
            if ("CARTAORECORRENTE".equals(codMeioPagamento) && 
                Boolean.TRUE.equals(this.recorrenciaAutorizada)) {
                return "PAGO";
            }

            if ("CARTAO".equals(codMeioPagamento) && 
                Boolean.TRUE.equals(this.meioPagamento.getUtilizadoParaLinkPagamento()) && 
                Boolean.TRUE.equals(this.recorrenciaAutorizada)) {
                return "PAGO";
            }
        }
        
        if (isVencida()) {
            return "VENCIDO";
        }
        return "A VENCER";
    }
    
    private LocalDateTime getDataBaseVencimento() {
        return this.dataVencimentoOriginal != null ? 
            this.dataVencimentoOriginal : this.dataVencimento;
    }
    

    public boolean isVencida() {
        LocalDateTime dataBase = getDataBaseVencimento();
        return dataBase != null && dataBase.isBefore(LocalDateTime.now());
    }
    

    public boolean isPagoCalculado() {
        return "PAGO".equals(calcularStatus());
    }
    

    public BigDecimal calcularValorAtualizado() {
        if (!"VENCIDO".equals(calcularStatus())) {
            return calcularValorTotal();
        }
        
        BigDecimal juros = calcularJuros();
        BigDecimal multa = calcularMulta();
        
        return calcularValorTotal().add(juros).add(multa);
    }
    

    public BigDecimal calcularJuros() {

        if (!"VENCIDO".equals(calcularStatus())) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal valorBase = calcularValorTotal();
        LocalDateTime dataBase = getDataBaseVencimento();
        long diasAtraso = ChronoUnit.DAYS.between(
            dataBase.toLocalDate(), 
            LocalDateTime.now().toLocalDate()
        );
        

        if (diasAtraso <= 0) {
            return BigDecimal.ZERO;
        }
        
        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorJurosDeMora() != null) {
            return this.carteiraBoleto.getValorJurosDeMora()
                .multiply(BigDecimal.valueOf(diasAtraso));
        } else if (this.empresa != null) {
            ParametroFinanceiro parametro = ParametroFinanceiroHelper.buscarPorEmpresa(this.empresa.getId());
            if (parametro != null && parametro.getPercentualJuro() != null) {
                BigDecimal taxaDiaria = parametro.getPercentualJuro()
                    .divide(BigDecimal.valueOf(30), 6, RoundingMode.HALF_UP)  // Converte mensal para diário
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP); // Converte percentual para decimal
                return valorBase.multiply(taxaDiaria)
                    .multiply(BigDecimal.valueOf(diasAtraso));
            }
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Calcula o valor de juros mensal (juros de 30 dias)
     */
    public BigDecimal calcularJuroMensal() {
        if (!"VENCIDO".equals(calcularStatus())) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal valorBase = calcularValorTotal();
        
        // Verifica se tem CarteiraBoleto com valor fixo por dia
        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorJurosDeMora() != null) {
            // Carteira usa valor fixo por dia, então juros de 30 dias
            return this.carteiraBoleto.getValorJurosDeMora()
                .multiply(BigDecimal.valueOf(30));
        } 
        // Se não tem carteira, usa o percentual do ParametroFinanceiro
        else if (this.empresa != null) {
            ParametroFinanceiro parametro = ParametroFinanceiroHelper.buscarPorEmpresa(this.empresa.getId());
            if (parametro != null && parametro.getPercentualJuro() != null) {
                // Percentual mensal aplicado sobre o valor base
                return valorBase.multiply(parametro.getPercentualJuro())
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            }
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Calcula o valor da multa
     */
    public BigDecimal calcularMulta() {
        // Usa o status calculado ao invés de isVencida() para considerar todas as regras de negócio
        if (!"VENCIDO".equals(calcularStatus())) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal valorBase = calcularValorTotal();
        
        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorMulta() != null) {
            return this.carteiraBoleto.getValorMulta();
        } else if (this.empresa != null) {
            ParametroFinanceiro parametro = ParametroFinanceiroHelper.buscarPorEmpresa(this.empresa.getId());
            if (parametro != null && parametro.getPercentualMora() != null) {
                BigDecimal taxaMulta = parametro.getPercentualMora()
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                return valorBase.multiply(taxaMulta);
            }
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Retorna o percentual de juros mensal da CarteiraBoleto ou ParametroFinanceiro
     */
    public BigDecimal getPercentualJuroMensal() {
        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorJurosDeMora() != null) {
            // CarteiraBoleto usa valor fixo por dia, não tem percentual mensal
            return null;
        } else if (this.empresa != null) {
            ParametroFinanceiro parametro = ParametroFinanceiroHelper.buscarPorEmpresa(this.empresa.getId());
            if (parametro != null) {
                return parametro.getPercentualJuro();
            }
        }
        return null;
    }

    /**
     * Retorna o percentual de multa da CarteiraBoleto ou ParametroFinanceiro
     */
    public BigDecimal getPercentualMultaCalculado() {
        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorMulta() != null) {
            // CarteiraBoleto usa valor fixo, não tem percentual
            return null;
        } else if (this.empresa != null) {
            ParametroFinanceiro parametro = ParametroFinanceiroHelper.buscarPorEmpresa(this.empresa.getId());
            if (parametro != null) {
                return parametro.getPercentualMora();
            }
        }
        return null;
    }

    /**
     * Calcula o valor de juros diário (juros de um único dia)
     */
    public BigDecimal calcularJuroDiario() {
        // Usa o status calculado ao invés de isVencida() para considerar todas as regras de negócio
        if (!"VENCIDO".equals(calcularStatus())) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal valorBase = calcularValorTotal();
        
        // Verifica se tem CarteiraBoleto com valor fixo por dia
        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorJurosDeMora() != null) {
            return this.carteiraBoleto.getValorJurosDeMora();
        } 
        // Se não tem carteira, usa o percentual do ParametroFinanceiro
        else if (this.empresa != null) {
            ParametroFinanceiro parametro = ParametroFinanceiroHelper.buscarPorEmpresa(this.empresa.getId());
            if (parametro != null && parametro.getPercentualJuro() != null) {
                // Percentual mensal / 30 dias = percentual diário
                // Aplica sobre o valor base
                BigDecimal taxaDiaria = parametro.getPercentualJuro()
                    .divide(BigDecimal.valueOf(30), 6, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                return valorBase.multiply(taxaDiaria);
            }
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Retorna o percentual de juros diário (percentual mensal / 30)
     */
    public BigDecimal getPercentualJuroDiario() {
        BigDecimal percentualMensal = getPercentualJuroMensal();
        if (percentualMensal != null) {
            return percentualMensal.divide(BigDecimal.valueOf(30), 6, RoundingMode.HALF_UP);
        }
        return null;
    }
}
