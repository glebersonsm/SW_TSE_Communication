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
@Table(name = "escolhaperiodomodelocota")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class EscolhaPeriodoModeloCota {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqescolhaperiodomodelocota")
    @SequenceGenerator(name = "seqescolhaperiodomodelocota", sequenceName = "seqescolhaperiodomodelocota", allocationSize = 1)
    @Column(name = "idescolhaperiodomodelocota")
    private Long id;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "mes")
    private Integer mes;

    @Column(name = "ativo")
    private Boolean ativo;

    @ManyToOne
    @JoinColumn(name = "idmodelocota")
    private ModeloCota modeloCota;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "inicioperiodo")
    private LocalDateTime inicioPeriodo;

    @Column(name = "fimperiodo")
    private LocalDateTime fimPeriodo;

    // Método estático para criar nova escolha período modelo cota
    static EscolhaPeriodoModeloCota novaEscolhaPeriodoModeloCota(Integer ano, Integer mes, 
            Boolean ativo, ModeloCota modeloCota, OperadorSistema responsavelCadastro, 
            Empresa empresa, LocalDateTime inicioPeriodo, LocalDateTime fimPeriodo) {
        
        EscolhaPeriodoModeloCota novaEscolha = new EscolhaPeriodoModeloCota();
        novaEscolha.setAno(ano);
        novaEscolha.setMes(mes);
        novaEscolha.setAtivo(ativo);
        novaEscolha.setModeloCota(modeloCota);
        novaEscolha.setResponsavelCadastro(responsavelCadastro);
        novaEscolha.setEmpresa(empresa);
        novaEscolha.setInicioPeriodo(inicioPeriodo);
        novaEscolha.setFimPeriodo(fimPeriodo);
        
        return novaEscolha;
    }
}
