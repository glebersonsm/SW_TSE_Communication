package com.sw.tse.domain.model.db;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.sw.tse.domain.model.db.Empresa;

@Entity
@Table(name = "parametrizacaohistoricomovbancaria")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ParametroFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // SERIAL implies identity
    @Column(name = "idparametrizacaohistoricomovbancaria")
    private Long idParametrizacaoHistoricoMovBancaria;

    @Column(name = "datacadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;
	
    @Column(name = "idrespcadastro")
    private Integer idRespCadastro;

    @Column(name = "idrespalteracao")
    private Integer idRespAlteracao;

    @ManyToOne()
    @JoinColumn(name = "idempresa")
    private Empresa empresa;

    @Column(name = "historicobaixacontasreceber", length = 2048)
    private String historicoBaixaContasReceber;

    @Column(name = "historicocancelamentocontasreceber", length = 2048)
    private String historicoCancelamentoContasReceber;

    @Column(name = "historicobaixacontaspagar", length = 2048)
    private String historicoBaixaContasPagar;

    @Column(name = "historicocancelamentocontaspagar", length = 2048)
    private String historicoCancelamentoContasPagar;

    @Column(name = "idtenant")
    private Integer idTenant;

    @Column(name = "percentualjuro", precision = 15, scale = 4)
    private BigDecimal percentualJuro;

    @Column(name = "percentualmora", precision = 15, scale = 4)
    private BigDecimal percentualMora;
}
