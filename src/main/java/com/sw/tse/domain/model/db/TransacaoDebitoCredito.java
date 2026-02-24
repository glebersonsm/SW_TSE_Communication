package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sw.tse.core.util.GenericCryptoStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transacaocartaocreditodebito")
@Getter
@Setter
@NoArgsConstructor
public class TransacaoDebitoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqtransacaocartaocreditodebito")
    @SequenceGenerator(name = "seqtransacaocartaocreditodebito", sequenceName = "seqtransacaocartaocreditodebito", allocationSize = 1)
    @Column(name = "idtransacaocartaocreditodebito")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", insertable = true, updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao", insertable = false, updatable = true)
    private LocalDateTime dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    // Dados da Transação
    @Column(name = "autorizado")
    private Boolean autorizado;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "statusgenerico", length = 50)
    private String statusGenerico;

    @Column(name = "nsu", length = 50)
    private String nsu;

    @Column(name = "tid", length = 50)
    private String tid;

    @Column(name = "codigoautorizacao", length = 50)
    private String codigoAutorizacao;

    @Column(name = "paymentid", length = 100)
    private String paymentId;

    @Column(name = "merchantorderid", length = 100)
    private String merchantOrderId;

    @Column(name = "valorreceber", precision = 15, scale = 2)
    private BigDecimal valorReceber;

    @Column(name = "qtdparcela")
    private Integer qtdParcela;

    @Column(name = "numerocartaomascarado", length = 20)
    private String numeroCartaoMascarado;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "nomeimpressocartao", length = 100)
    private String nomeImpressoCartao;

    @Column(name = "mensagemretorno", length = 500)
    private String mensagemRetorno;

    @Column(name = "codigoretorno", length = 50)
    private String codigoRetorno;

    @Column(name = "bloqueadoparaprocessamento")
    private Boolean bloqueadoParaProcessamento;

    @Column(name = "gatewaypagamento", length = 100)
    private String gatewayPagamento;

    // Campos adicionais
    @Column(name = "datavencimento", nullable = false)
    private LocalDateTime dataVencimento;

    @Column(name = "idbandeirasaceitas") // Nullable - não temos essa informação do portal
    private Integer idBandeirasAceitas;

    @Column(name = "idbandeiracartao") // Nullable - não temos essa informação do portal
    private Integer idBandeiraCartao;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "numerocartao", nullable = false, length = 250)
    private String numeroCartao;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "codsegurancacartao", nullable = false, length = 250)
    private String codSegurancaCartao;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "mesvalidadecartao", nullable = false, length = 250)
    private String mesValidadeCartao;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "anovalidadecartao", nullable = false, length = 250)
    private String anoValidadeCartao;

    @Column(name = "estornado", nullable = false)
    private Boolean estornado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontrato")
    private Contrato contrato;

    @Column(name = "nomepessoa", length = 250)
    private String nomePessoa;
}
