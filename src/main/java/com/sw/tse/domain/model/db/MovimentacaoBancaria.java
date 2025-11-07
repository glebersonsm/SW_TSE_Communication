package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "movimentacaobancaria")
@Setter
@Getter
@NoArgsConstructor
public class MovimentacaoBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqmovimentacaobancaria")
    @SequenceGenerator(name = "seqmovimentacaobancaria", sequenceName = "seqmovimentacaobancaria", allocationSize = 1)
    @Column(name = "idmovimentacaobancaria")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "data")
    private LocalDateTime data;

    @Column(name = "historico", length = 2048)
    private String historico;

    @Column(name = "valor", precision = 15, scale = 4)
    private BigDecimal valor;

    @Column(name = "debitocreditomovimentacaobancaria")
    private Integer debitoCreditoMovimentacaoBancaria; // 0 = Crédito, 1 = Débito

    @Column(name = "estornado")
    private Boolean estornado = false;

    @ManyToOne
    @JoinColumn(name = "idoperadorestorno")
    private OperadorSistema operadorEstorno;

    @Column(name = "dataexclusao")
    private LocalDateTime dataExclusao;

    @ManyToOne
    @JoinColumn(name = "idoperadorexclusao")
    private OperadorSistema operadorExclusao;

    @Column(name = "lancamentomanual")
    private Boolean lancamentoManual = false;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne
    @JoinColumn(name = "idcontamovbancaria")
    private ContaMovimentacaoBancaria contaMovimentacaoBancaria;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "transferencia")
    private Boolean transferencia = false;

    @Column(name = "idmovimentacaobancariatransferenciaorigem")
    private Long idMovimentacaoBancariaTransferenciaOrigem;

    @Column(name = "numerodocumento", length = 50)
    private String numeroDocumento;

    @Column(name = "idtipolancamentomovimentacaobancaria")
    private Integer idTipoLancamentoMovimentacaoBancaria;

    @Column(name = "lancamentoconciliado")
    private Boolean lancamentoConciliado = false;

    @Column(name = "dataestorno")
    private LocalDateTime dataEstorno;
}

