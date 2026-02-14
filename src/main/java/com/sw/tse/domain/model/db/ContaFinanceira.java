package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    @Column(name = "chequecompensado")
    private Boolean chequeCompensado;

    @Column(name = "chequedevolvido")
    private Boolean chequeDevolvido;

    @Column(name = "retidocomomultadecancelamento")
    private Boolean retidoComoMultaCancelamento;

    @Column(name = "tituloantecipado")
    private Boolean tituloAntecipado;

    @Column(name = "valorretidocomomultacancelamento", precision = 15, scale = 2)
    private BigDecimal valorRetidoComoMultaCancelamento;

    @Column(name = "saldotransferido", precision = 15, scale = 2)
    private BigDecimal saldoTransferido;

    @Column(name = "pontosconsumidosbaixa")
    private Integer pontosConsumidoBaixa;

    @Column(name = "idunidadenegocio")
    private Long idUnidadeNegocio;

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
        BigDecimal valorCorrecao = this.valorAcrescimoAcumuladoCorrecaoMonetaria != null
                ? this.valorAcrescimoAcumuladoCorrecaoMonetaria
                : BigDecimal.ZERO;
        BigDecimal valorAcrescimo = this.getValorAcrescimo() != null ? this.getValorAcrescimo() : BigDecimal.ZERO;
        BigDecimal valorDesconto = this.valorDesconto != null ? this.valorDesconto : BigDecimal.ZERO;
        BigDecimal valorDescontoManual = this.valorDescontoManual != null ? this.valorDescontoManual : BigDecimal.ZERO;

        return valorParcela.add(valorCorrecao).add(valorAcrescimo).subtract(valorDesconto)
                .subtract(valorDescontoManual);
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
        return this.dataVencimentoOriginal != null ? this.dataVencimentoOriginal : this.dataVencimento;
    }

    public boolean isVencida() {
        LocalDateTime dataBase = getDataBaseVencimento();
        // Compara apenas a data (ignorando hora) para que vencimento "hoje" não seja
        // considerado vencido
        return dataBase != null && dataBase.toLocalDate().isBefore(LocalDate.now());
    }

    /**
     * Verifica se a conta deve ter juros e multas calculados.
     * Retorna true se a conta está vencida E não está paga.
     * Para destinoContaFinanceira = 'P', calcula apenas se não estiver paga.
     */
    private boolean deveCalcularJurosMultas() {
        // Se a conta está paga (via campo pago ou tipoHistorico), nunca calcula juros e
        // multas
        if (Boolean.TRUE.equals(this.pago) ||
                "BAIXADO".equals(this.tipoHistorico) ||
                "TRANSFERIDO".equals(this.tipoHistorico) ||
                "BAIXADOCARTACREDITO".equals(this.tipoHistorico)) {
            return false;
        }

        // Verificar outras condições de pagamento via meio de pagamento
        if (this.meioPagamento != null && this.meioPagamento.getCodMeioPagamento() != null) {
            String codMeioPagamento = this.meioPagamento.getCodMeioPagamento().toUpperCase();

            if ("CARTAO".equals(codMeioPagamento) &&
                    Boolean.FALSE.equals(this.meioPagamento.getUtilizadoParaLinkPagamento())) {
                return false; // Cartão sem link de pagamento = pago
            }

            if ("CARTAORECORRENTE".equals(codMeioPagamento) &&
                    Boolean.TRUE.equals(this.recorrenciaAutorizada)) {
                return false; // Cartão recorrente autorizado = pago
            }

            if ("CARTAO".equals(codMeioPagamento) &&
                    Boolean.TRUE.equals(this.meioPagamento.getUtilizadoParaLinkPagamento()) &&
                    Boolean.TRUE.equals(this.recorrenciaAutorizada)) {
                return false; // Cartão com link e recorrente autorizado = pago
            }
        }

        // Se destinoContaFinanceira = 'P' e não está paga, calcula juros e multas
        if ("P".equalsIgnoreCase(this.destinoContaFinanceira)) {
            return true;
        }

        // Caso contrário, só calcula se estiver vencida e não paga
        return isVencida();
    }

    public boolean isPagoCalculado() {
        return "PAGO".equals(calcularStatus());
    }

    public BigDecimal calcularValorAtualizado() {
        BigDecimal valorTotal = calcularValorTotal();

        if (!deveCalcularJurosMultas()) {
            // Conta está paga - não calcula juros e multas dinamicamente
            // Mas inclui valores de juros e multa já salvos no banco de dados
            BigDecimal jurosSalvos = this.valorJuros != null ? this.valorJuros : BigDecimal.ZERO;
            BigDecimal multaSalva = this.valorMulta != null ? this.valorMulta : BigDecimal.ZERO;

            return valorTotal.add(jurosSalvos).add(multaSalva);
        }

        // Conta não está paga - calcula juros e multas dinamicamente
        BigDecimal juros = calcularJuros();
        BigDecimal multa = calcularMulta();

        return valorTotal.add(juros).add(multa);
    }

    public BigDecimal calcularJuros() {

        if (!deveCalcularJurosMultas()) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorBase = calcularValorTotal();
        LocalDateTime dataBase = getDataBaseVencimento();
        long diasAtraso = ChronoUnit.DAYS.between(
                dataBase.toLocalDate(),
                LocalDateTime.now().toLocalDate());

        if (diasAtraso <= 0) {
            return BigDecimal.ZERO;
        }

        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorJurosDeMora() != null) {

            if (this.carteiraBoleto.getValorJurosDeMora().equals(BigDecimal.ZERO)) {
                return BigDecimal.ZERO;
            }

            BigDecimal percentualTaxaJuros = this.carteiraBoleto.getValorJurosDeMora()
                    .divide(BigDecimal.valueOf(30), 6, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

            BigDecimal valorJurosDia = valorBase
                    .multiply(percentualTaxaJuros);

            BigDecimal valorJurosTotal = valorJurosDia.multiply(BigDecimal.valueOf(diasAtraso));

            return valorJurosTotal;

        } else if (this.empresa != null) {
            ParametroFinanceiro parametro = ParametroFinanceiroHelper.buscarPorEmpresa(this.empresa.getId());
            if (parametro != null && parametro.getPercentualJuro() != null) {
                BigDecimal taxaDiaria = parametro.getPercentualJuro()
                        .divide(BigDecimal.valueOf(30), 6, RoundingMode.HALF_UP) // Converte mensal para diário
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP); // Converte percentual para decimal
                BigDecimal valorJuros = valorBase.multiply(taxaDiaria)
                        .multiply(BigDecimal.valueOf(diasAtraso));

                return valorJuros;
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Calcula o valor de juros mensal (juros de 30 dias)
     */
    public BigDecimal calcularJuroMensal() {
        if (!deveCalcularJurosMultas()) {
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
        // Considera contas vencidas OU com destinoContaFinanceira = 'P'
        if (!deveCalcularJurosMultas()) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorBase = calcularValorTotal();

        if (this.carteiraBoleto != null && this.carteiraBoleto.getValorMulta() != null) {
            BigDecimal taxaMulta = this.carteiraBoleto.getValorMulta()
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            return valorBase.multiply(taxaMulta);
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
        // Considera contas vencidas OU com destinoContaFinanceira = 'P'
        if (!deveCalcularJurosMultas()) {
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

    /**
     * Cria uma nova conta financeira consolidada para pagamento via portal
     * 
     * @param contaBase         Conta base para copiar dados principais
     * @param valorTotal        Valor total do pagamento
     * @param totalAcrescimo    Total de acréscimos
     * @param totalJuros        Total de juros
     * @param totalMulta        Total de multas
     * @param totalCorrecao     Total de correções
     * @param transacaoId       ID da transação de débito/crédito
     * @param codigoAutorizacao Código de autorização do gateway
     * @param adquirente        Nome da adquirente (GETNET ou REDE)
     * @param nsu               NSU da transação
     * @param idPedidoPortal    ID do pedido do portal (para guidMerchantOrderId)
     * @param meioPagamento     Meio de pagamento (CARTAO)
     * @param origemConta       Tipo de origem da conta
     * @param responsavel       Operador responsável
     * @param bandeiraCartao    Bandeira do cartão (para determinar prazo de
     *                          pagamento)
     * @param dataAutorizacao   Data da autorização do pagamento
     * @return Nova conta consolidada
     */
    public static ContaFinanceira criarContaConsolidadaPagamentoPortal(
            ContaFinanceira contaBase,
            BigDecimal valorTotal,
            BigDecimal totalAcrescimo,
            BigDecimal totalJuros,
            BigDecimal totalMulta,
            BigDecimal totalCorrecao,
            Long transacaoId,
            String codigoAutorizacao,
            String adquirente,
            String nsu,
            String idPedidoPortal,
            MeioPagamento meioPagamento,
            TipoOrigemContaFinanceira origemConta,
            OperadorSistema responsavel,
            BandeiraCartao bandeiraCartao,
            LocalDateTime dataAutorizacao,
            ContaMovimentacaoBancaria contaMovimentacaoBancaria,
            boolean isPix,
            String pixCopiaECola,
            LocalDateTime dataGeracaoPix,
            OperadorSistema operadorPadrao) {

        ContaFinanceira contaNova = new ContaFinanceira();

        // Dados básicos da conta base
        contaNova.empresa = contaBase.getEmpresa();
        contaNova.pessoa = contaBase.getPessoa();
        contaNova.contrato = contaBase.getContrato();
        contaNova.contaMovimentacaoBancaria = contaMovimentacaoBancaria; // Usar a conta passada como parâmetro
        contaNova.responsavelCadastro = responsavel;
        contaNova.meioPagamento = meioPagamento;
        contaNova.origemConta = origemConta;

        // Valores
        // contaNova.valorReceber = valorTotal; // ValorReceber deve ser a soma das
        // contas (configurado depois)
        contaNova.valorParcela = valorTotal;
        contaNova.valorReceber = valorTotal;
        contaNova.valorAcrescimo = totalAcrescimo;
        contaNova.valorJuros = totalJuros;
        contaNova.valorMulta = totalMulta;
        contaNova.valorAcrescimoAcumuladoCorrecaoMonetaria = totalCorrecao;
        contaNova.destinoContaFinanceira = "R";
        contaNova.guidMerchantOrderId = idPedidoPortal;

        // Diferenciação PIX vs CARTÃO
        if (isPix) {
            // === PIX: Pagamento instantâneo, já recebido ===
            contaNova.tipoHistorico = "BAIXADO";
            contaNova.recorrenciaAutorizada = false;
            contaNova.numeroDocumento = codigoAutorizacao;
            contaNova.dataVencimento = LocalDate.now().atStartOfDay(); // Hoje, sem hora
            contaNova.historico = "Pagamento Portal - PIX - NSU: " + nsu;
            contaNova.pago = true;
            contaNova.dataPagamento = dataAutorizacao.toLocalDate().atStartOfDay(); // Sem hora
            contaNova.dataBaixa = dataAutorizacao.truncatedTo(ChronoUnit.SECONDS); // Com hora, sem nanossegundos
            contaNova.dataLiquidacao = dataAutorizacao.toLocalDate().atStartOfDay(); // Sem hora
            contaNova.valorRecebido = valorTotal; // Valor pago no PIX
            contaNova.historicoBaixa = "Pagamento PIX recebido - NSU: " + nsu;
            contaNova.responsavelBaixa = operadorPadrao; // Usar operador padrão
            contaNova.idTransacaoCartaoCreditoDebito = null; // PIX não tem transação de cartão

            // Campos PIX específicos
            contaNova.pixCopiaECola = pixCopiaECola;
            contaNova.pixQrCode = pixCopiaECola; // Mesmo valor do PIX copia e cola
            contaNova.txId = codigoAutorizacao; // TxId = identificador único do PIX (usado nas consultas)
            contaNova.dataGeracaoPix = dataGeracaoPix;
        } else {
            // === CARTÃO: Aguarda repasse da operadora ===
            LocalDateTime dataVencimento = calcularDataVencimento(bandeiraCartao, dataAutorizacao);
            contaNova.tipoHistorico = "ATIVO";
            contaNova.recorrenciaAutorizada = true;
            contaNova.numeroDocumento = codigoAutorizacao;
            contaNova.dataVencimento = dataVencimento;
            contaNova.historico = "Pagamento Portal - Cartão " + adquirente + " - NSU: " + nsu;
            contaNova.pago = false;
            contaNova.idTransacaoCartaoCreditoDebito = transacaoId;
        }

        return contaNova;
    }

    /**
     * Calcula a data de vencimento baseada na configuração da bandeira do cartão
     * Retorna apenas data (sem hora, minuto, segundo) - meia-noite do dia
     * 
     * @param bandeiraCartao  Bandeira do cartão
     * @param dataAutorizacao Data da autorização
     * @return Data de vencimento calculada (sem hora)
     */
    private static LocalDateTime calcularDataVencimento(BandeiraCartao bandeiraCartao, LocalDateTime dataAutorizacao) {
        int diasParaAdicionar;

        // Se a bandeira tem quantidade de dias configurada e é maior que zero
        if (bandeiraCartao != null
                && bandeiraCartao.getQuantidadeDiaPagamento() != null
                && bandeiraCartao.getQuantidadeDiaPagamento() > 0) {
            diasParaAdicionar = bandeiraCartao.getQuantidadeDiaPagamento();
        } else {
            // Caso contrário, usa 30 dias como padrão
            diasParaAdicionar = 30;
        }

        // Retorna apenas data (sem hora) - meia-noite do dia
        return dataAutorizacao.toLocalDate()
                .plusDays(diasParaAdicionar)
                .atStartOfDay(); // Retorna LocalDateTime com hora 00:00:00
    }

    /**
     * Cancela esta conta financeira
     * 
     * @param responsavel Operador responsável pelo cancelamento
     * @param motivo      Motivo do cancelamento
     */
    public void cancelar(OperadorSistema responsavel, String motivo) {
        this.tipoHistorico = "CANCELADO";
        this.historicoCancelamento = motivo;
        this.dataCancelamento = LocalDateTime.now();
        this.responsavelCancelamento = responsavel;
    }

    /**
     * Configura campos específicos para conta de negociação via portal
     */
    public void configurarCamposNegociacaoPortal(
            Long idUnidadeNegocio,
            Integer numeroParcela,
            BigDecimal taxaCartao,
            BigDecimal descontoTaxaCartao,
            BigDecimal valorReceber,
            BigDecimal valorParcela,
            BigDecimal valorDesconto,
            BigDecimal valorAcrescimo,
            BigDecimal valorDescontoManual,
            BigDecimal valorJuros,
            BigDecimal valorMulta,
            Long idBandeirasAceitas) {

        this.idUnidadeNegocio = idUnidadeNegocio;
        this.numeroParcela = numeroParcela;
        this.historico = "Pagamento feito via portal cliente";

        // Configurações de cartão
        this.assinaturaEmArquivoCartao = false;
        this.qtdParcelasCartao = 0;
        this.codSegurancaCartao = 0;
        this.taxaCartao = taxaCartao;
        this.descontoTaxaCartao = descontoTaxaCartao;
        this.idBandeirasAceitas = idBandeirasAceitas;

        // Campos booleanos
        this.chequeCompensado = false;
        this.chequeDevolvido = false;
        this.boletoRegistrado = false;
        this.retidoComoMultaCancelamento = false;
        this.tituloAntecipado = false;

        // Taxa boleto
        this.taxaBoleto = BigDecimal.ZERO;

        // Valores
        this.valorReceber = valorReceber;
        this.valorParcela = valorParcela;
        this.valorDesconto = valorDesconto;
        this.valorAcrescimo = valorAcrescimo;
        this.valorDescontoManual = valorDescontoManual;
        this.valorJuros = valorJuros;
        this.valorMulta = valorMulta;

        // Campos zerados
        this.taxaJurosFinancMensal = 0.0;
        this.valorJurosFinanc = BigDecimal.ZERO;
        this.valorCapital = BigDecimal.ZERO;
        this.taxaJuros = 0.0;
        this.pontosConsumidoBaixa = 0;
        // Preserva valorRecebido se já foi definido (PIX), só zera se ainda não foi
        // (CARTAO)
        if (this.valorRecebido == null || this.valorRecebido.compareTo(BigDecimal.ZERO) == 0) {
            this.valorRecebido = BigDecimal.ZERO;
        }
        this.saldoTransferido = BigDecimal.ZERO;
        this.valorRetidoComoMultaCancelamento = BigDecimal.ZERO;
    }

    /**
     * Atualiza os dados da conta financeira para pagamento via portal
     */
    public void atualizarDadosPagamentoPortal(
            OperadorSistema responsavel,
            ContaMovimentacaoBancaria contaMovimentacaoBancaria,
            MeioPagamento meioPagamento,
            BigDecimal valorTotal,
            BigDecimal valorJurosCobrado,
            BigDecimal valorMultaCobrada,
            String guidMerchantOrderId,
            BandeiraCartao bandeiraCartao,
            boolean isPix,
            String codigoAutorizacao,
            String nsu,
            String adquirente,
            Long idTransacaoCartao,
            OperadorSistema operadorPadrao,
            String pixCopiaECola,
            String txId,
            LocalDateTime dataGeracaoPix) {

        // Atualizar dados básicos
        this.contaMovimentacaoBancaria = contaMovimentacaoBancaria;
        this.responsavelAlteracao = responsavel;
        this.meioPagamento = meioPagamento;

        // Atualizar valores
        // this.valorReceber = valorTotal; // Manter valor original
        this.valorJuros = valorJurosCobrado;
        this.valorMulta = valorMultaCobrada;

        this.guidMerchantOrderId = guidMerchantOrderId;

        // Taxas do cartão
        if (!isPix && bandeiraCartao != null) {
            BigDecimal taxa = bandeiraCartao.getTaxaOperacao() != null
                    ? BigDecimal.valueOf(bandeiraCartao.getTaxaOperacao())
                    : BigDecimal.ZERO;
            this.taxaCartao = taxa;

            BigDecimal descontoTaxa = valorTotal.multiply(taxa)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            this.descontoTaxaCartao = descontoTaxa;

            if (bandeiraCartao.getIdBandeirasAceitas() != null) {
                this.idBandeirasAceitas = bandeiraCartao.getIdBandeirasAceitas().longValue();
            }
        }

        // Diferenciação PIX vs CARTÃO
        if (isPix) {
            this.tipoHistorico = "BAIXADO";
            this.recorrenciaAutorizada = false;
            this.numeroDocumento = codigoAutorizacao;
            if (this.dataVencimentoOriginal == null) {
                this.dataVencimentoOriginal = this.dataVencimento;
            }
            this.historico = "Conta paga pelo portal do Cliente via Pix";
            this.pago = true;

            LocalDateTime dataAgora = LocalDateTime.now();
            this.dataPagamento = dataAgora.toLocalDate().atStartOfDay();
            this.dataBaixa = dataAgora.truncatedTo(ChronoUnit.SECONDS);
            this.dataLiquidacao = dataAgora.toLocalDate().atStartOfDay();
            this.valorRecebido = valorTotal;
            this.historicoBaixa = "Pagamento PIX recebido - NSU: " + nsu;
            this.responsavelBaixa = operadorPadrao;

            if (!this.getTaxaCartao().equals(BigDecimal.ZERO)) {
                this.setTaxaCartao(BigDecimal.ZERO);
                this.setDescontoTaxaCartao(BigDecimal.ZERO);
            }

            this.pixCopiaECola = pixCopiaECola;
            this.pixQrCode = pixCopiaECola;
            this.txId = codigoAutorizacao; // TxId = identificador único do PIX
            this.dataGeracaoPix = dataGeracaoPix;
        } else {
            LocalDateTime dataVencimentoCalc = calcularDataVencimento(bandeiraCartao, LocalDateTime.now());
            this.tipoHistorico = "ATIVO";
            this.recorrenciaAutorizada = true;
            this.numeroDocumento = codigoAutorizacao;
            this.dataVencimento = dataVencimentoCalc;
            this.historico = "Alterada/Paga Portal - Cartão "
                    + (adquirente != null ? adquirente : "Cartão") + " - NSU: " + nsu;
            this.pago = false;
            // idTransacaoCartaoCreditoDebito pode ser null
            if (idTransacaoCartao != null) {
                this.idTransacaoCartaoCreditoDebito = idTransacaoCartao;
            }
        }
    }
}
