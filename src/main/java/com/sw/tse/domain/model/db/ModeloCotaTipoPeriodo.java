package com.sw.tse.domain.model.db;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "modelocotatipoperiodo")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ModeloCotaTipoPeriodo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqmodelocotatipoperiodo")
    @SequenceGenerator(name = "seqmodelocotatipoperiodo", sequenceName = "seqmodelocotatipoperiodo", allocationSize = 1)
    @Column(name = "idmodelocotatipoperiodo")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idmodelocota")
    private ModeloCota modeloCota;

    @ManyToOne
    @JoinColumn(name = "idtipoperiodoutilizacao")
    private TipoPeriodoUtilizacao tipoPeriodoUtilizacao;

    @Column(name = "qtdmaximautilizacoes")
    private Integer qtdMaximaUtilizacoes;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    // Método estático para criar novo modelo cota tipo período
    static ModeloCotaTipoPeriodo novoModeloCotaTipoPeriodo(ModeloCota modeloCota, 
            TipoPeriodoUtilizacao tipoPeriodoUtilizacao, Integer qtdMaximaUtilizacoes) {
        
        ModeloCotaTipoPeriodo novoModeloTipoPeriodo = new ModeloCotaTipoPeriodo();
        novoModeloTipoPeriodo.setModeloCota(modeloCota);
        novoModeloTipoPeriodo.setTipoPeriodoUtilizacao(tipoPeriodoUtilizacao);
        novoModeloTipoPeriodo.setQtdMaximaUtilizacoes(qtdMaximaUtilizacoes);
        
        return novoModeloTipoPeriodo;
    }
}
