package com.sw.tse.domain.model.db;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bandeiracartao")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class BandeiraCartao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqbandeiracartao")
    @SequenceGenerator(name = "seqbandeiracartao", sequenceName = "seqbandeiracartao", allocationSize = 1)
    @Column(name = "idbandeiracartao")
    private Integer idBandeiraCartao;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "nomeestabelecimento", length = 512)
    private String nomeEstabelecimento;

    @Column(name = "bandeira", length = 15)
    private String bandeira;

    @Column(name = "operacao", length = 15)
    private String operacao;

    @Column(name = "taxaoperacao")
    private Double taxaOperacao;

    @Column(name = "parcelainicialcontrato")
    private Integer parcelaInicialContrato;

    @Column(name = "parcelafinalcontrato")
    private Integer parcelaFinalContrato;

    @Column(name = "ativo")
    private Boolean ativo;

    @Column(name = "contacontabil", length = 15)
    private String contaContabil;

    @Column(name = "quantidadediapagamento")
    private Integer quantidadeDiaPagamento;

    @Column(name = "idcontamovbancaria")
    private Integer idContaMovBancaria;

    @CreationTimestamp
    @Column(name = "datacadastro")
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "idplanofinanceirocontataxacartao")
    private Integer idPlanoFinanceiroContaTaxaCartao;

    @Column(name = "idplanofinanceirocontarecorrencia")
    private Integer idPlanoFinanceiroContaRecorrencia;

    @Column(name = "idplanofinanceirocancelamento")
    private Integer idPlanoFinanceiroCancelamento;

    @Column(name = "idplanofinanceirovenda")
    private Integer idPlanoFinanceiroVenda;

    @Column(name = "idplanofinanceirovendafinanciamento")
    private Integer idPlanoFinanceiroVendaFinanciamento;

    @Column(name = "idbandeirasaceitas")
    private Integer idBandeirasAceitas;

    @Column(name = "idplanofinanceirobaixarecebimento")
    private Integer idPlanoFinanceiroBaixaRecebimento;

    @Column(name = "idplanofinanceirobaixaparcialrecebimento")
    private Integer idPlanoFinanceiroBaixaParcialRecebimento;

    @Column(name = "idimportacao", length = 250)
    private String idImportacao;

    @Column(name = "idcentrocusto")
    private Integer idCentroCusto;
}

