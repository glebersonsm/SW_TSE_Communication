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
@Table(name = "tipoperiodoutilizacao")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TipoPeriodoUtilizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqtipoperiodoutilizacao")
    @SequenceGenerator(name = "seqtipoperiodoutilizacao", sequenceName = "seqtipoperiodoutilizacao", allocationSize = 1)
    @Column(name = "idtipoperiodoutilizacao")
    private Long id;

    @Column(name = "descricao", length = 600)
    private String descricao;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "peso")
    private Integer peso;

    // Relacionamento com Empresa (tenant)
    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    // Método estático para criar novo tipo de período de utilização
    static TipoPeriodoUtilizacao novoTipoPeriodoUtilizacao(String descricao, Integer peso, 
            Empresa empresa, OperadorSistema responsavelCadastro) {
        
        TipoPeriodoUtilizacao novoTipo = new TipoPeriodoUtilizacao();
        novoTipo.setDescricao(descricao);
        novoTipo.setPeso(peso);
        novoTipo.setEmpresa(empresa);
        novoTipo.setResponsavelCadastro(responsavelCadastro);
        
        return novoTipo;
    }
}