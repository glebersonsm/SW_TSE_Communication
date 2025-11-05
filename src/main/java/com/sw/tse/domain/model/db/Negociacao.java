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
@Table(name = "negociacao")
@Getter
@Setter
@NoArgsConstructor
public class Negociacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqnegociacao")
    @SequenceGenerator(name = "seqnegociacao", sequenceName = "seqnegociacao", allocationSize = 1)
    @Column(name = "idnegociacao")
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

    @Column(name = "statusnegociacao")
    private Integer statusNegociacao;

    @Column(name = "datacancelamento")
    private LocalDateTime dataCancelamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcancelamento")
    private OperadorSistema responsavelCancelamento;

    @Column(name = "idlotedestinatariocrm")
    private Long idLoteDestinatarioCrm;

    @Column(name = "origemnegociacao")
    private Integer origemNegociacao;
}

