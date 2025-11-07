package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sw.tse.core.util.GenericCryptoStringConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
@Table(name = "contamovbancaria")
@Setter(value = AccessLevel.PRIVATE)
@Getter()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ContaMovimentacaoBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcontamovbancaria")
    @SequenceGenerator(name = "seqcontamovbancaria", sequenceName = "seqcontamovbancaria", allocationSize = 1)
    @Column(name = "idcontamovbancaria")
    private Long id;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "idbanco")
    private Banco banco;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "titularconta", length = 80)
    private String titularConta;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "agencia", length = 128)
    private String agencia;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "digitoagencia", length = 64)
    private String digitoAgencia;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "nroconta", length = 128)
    private String numeroConta;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "digitoconta", length = 64)
    private String digitoConta;

    @Convert(converter = GenericCryptoStringConverter.class)
    @Column(name = "tipoconta", length = 64)
    private String tipoConta;

    @Column(name = "operacao", columnDefinition = "TEXT")
    private String operacao;

    @Column(name = "inativa")
    private Boolean inativa;
}
