package com.sw.tse.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessarPagamentoAprovadoTseDto {
    private Long idEmpresaTse;
    private Long idTorreTse;
    private Long idContratoTse;
    private Long idPessoaTse;
    private Long idUsuarioLogado; // ID do usuário que fez o pagamento (vem do JWT)

    // Dados da Transação
    private String idTransacao; // IdPedido da transacaoPagamento
    private LocalDateTime dataPagamento;
    private String paymentId; // ID retornado pela GetNet (somente GetNet)
    private String nsu;
    private String tid;
    private String codigoAutorizacao;
    private String codigoRetorno; // Código de retorno da operadora
    private String mensagemRetorno; // Mensagem de retorno da operadora
    private String status; // Status da operadora (APROVADA, NEGADA, ERRO)
    private String meioPagamento; // CARTAO ou PIX
    private BigDecimal valorTotal;
    private Integer numeroParcelas;
    private String numeroCartaoMascarado; // Formato: primeiros4 **** **** ultimos4
    private String nomeImpressoCartao;
    private String adquirente; // GETNET ou REDE
    private Integer idBandeira; // ID da bandeira do cartão (para buscar BandeiraCartao)

    // Dados completos do cartão (serão criptografados ao salvar)
    private String numeroCartao; // Número completo do cartão
    private String codigoSegurancaCartao; // CVV
    private String mesValidadeCartao; // Mês de validade
    private String anoValidadeCartao; // Ano de validade

    // Conta de Movimentação Bancária do Gateway
    private Long idContaMovimentacaoBancaria; // ID da conta de movimentação configurada no gateway

    // Dados PIX (para preencher na conta financeira)
    private String pixCopiaECola; // Código PIX copia e cola
    private LocalDateTime dataGeracaoPix; // Data/hora de geração do QR Code PIX

    // Lista de feriados para cálculo do próximo dia útil de compensação
    private List<LocalDate> feriados;

    // Contas Financeiras Selecionadas (cada uma com seu juros e multa calculados em
    // tela)
    // tela)
    private List<ContaFinanceiraParaPagamentoDto> contasFinanceiras;
}
