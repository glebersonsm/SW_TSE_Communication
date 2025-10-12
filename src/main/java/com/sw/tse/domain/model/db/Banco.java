package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "banco")
@Setter(value = AccessLevel.PRIVATE)
@Getter()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Banco {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqbanco")
    @SequenceGenerator(name = "seqbanco", sequenceName = "seqbanco", allocationSize = 1)
    @Column(name = "idbanco")
    private Long id;

    @Column(name = "codbanco", length = 10)
    private String codigoBanco;

    @Column(name = "descricao", length = 120)
    private String descricao;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "numerosequencialnsa")
    private Integer numeroSequencialNsa;

    @Column(name = "codigoconvenio", columnDefinition = "TEXT")
    private String codigoConvenio;

    @Column(name = "debitoautomatico")
    private Boolean debitoAutomatico;

    @Column(name = "nomeempresanobanco", columnDefinition = "TEXT")
    private String nomeEmpresaNoBanco;

    @Column(name = "codigoispb", length = 8)
    private String codigoIspb;
}
