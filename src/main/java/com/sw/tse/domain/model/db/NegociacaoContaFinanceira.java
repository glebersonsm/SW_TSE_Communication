package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "negociacaocontafinanceira")
@Getter
@Setter
@NoArgsConstructor
public class NegociacaoContaFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqnegociacaocontafinanceira")
    @SequenceGenerator(name = "seqnegociacaocontafinanceira", sequenceName = "seqnegociacaocontafinanceira", allocationSize = 1)
    @Column(name = "idnegociacaocontafinanceira")
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idnegociacao")
    private Negociacao negociacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontafinanceira")
    private ContaFinanceira contaFinanceira;

    @Column(name = "tiponegociacao")
    private Integer tipoNegociacao; // 1 = Nova, 2 = Cancelada, 3 = Alterada

    @Column(name = "contafinanceirajson", columnDefinition = "TEXT")
    private String contaFinanceiraJson;
}
