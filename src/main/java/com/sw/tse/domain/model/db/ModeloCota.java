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
@Table(name = "modelocota")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ModeloCota {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqmodelocota")
    @SequenceGenerator(name = "seqmodelocota", sequenceName = "seqmodelocota", allocationSize = 1)
    @Column(name = "idmodelocota")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idgrupocota")
    private GrupoCota grupoCota;

    @Column(name = "qtdperiodos")
    private Integer qtdPeriodos;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "idimportacao", length = 100)
    private String idImportacao;

    // Método estático para criar novo modelo de cota
    static ModeloCota novoModeloCota(String descricao, GrupoCota grupoCota, 
            Integer qtdPeriodos, Empresa empresa) {
        
        ModeloCota novoModelo = new ModeloCota();
        novoModelo.setDescricao(descricao);
        novoModelo.setGrupoCota(grupoCota);
        novoModelo.setQtdPeriodos(qtdPeriodos);
        novoModelo.setEmpresa(empresa);
        
        return novoModelo;
    }
}
