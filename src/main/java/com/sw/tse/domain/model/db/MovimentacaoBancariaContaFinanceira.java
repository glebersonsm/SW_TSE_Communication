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
@Table(name = "movimentacaobancariacontafinanceira")
@Setter
@Getter
@NoArgsConstructor
public class MovimentacaoBancariaContaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqmovimentacaobancariacontafinanceira")
    @SequenceGenerator(name = "seqmovimentacaobancariacontafinanceira", sequenceName = "seqmovimentacaobancariacontafinanceira", allocationSize = 1)
    @Column(name = "idmovimentacaobancariacontafinanceira")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne
    @JoinColumn(name = "idmovimentacaobancaria")
    private MovimentacaoBancaria movimentacaoBancaria;

    @ManyToOne
    @JoinColumn(name = "idcontafinanceira")
    private ContaFinanceira contaFinanceira;

    @Column(name = "valor", precision = 15, scale = 4)
    private BigDecimal valor;

    @Column(name = "estornado")
    private Boolean estornado = false;

    @Column(name = "dataestorno")
    private LocalDateTime dataEstorno;

    @ManyToOne
    @JoinColumn(name = "idoperadorestorno")
    private OperadorSistema operadorEstorno;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;
}

