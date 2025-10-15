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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "faixaetaria")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class FaixaEtaria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqfaixaetaria")
    @SequenceGenerator(name = "seqfaixaetaria", sequenceName = "seqfaixaetaria", allocationSize = 1)
    @Column(name = "idfaixaetaria")
    private Long id;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "sigla", length = 10, nullable = false)
    private String sigla;

    @Column(name = "idintegracao")
    private Integer idIntegracao;

    @Column(name = "ativo")
    private Boolean ativo;

    @CreationTimestamp
    @Column(name = "datacadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "ispagante")
    private Boolean isPagante;

    @Column(name = "isisentopontos")
    private Boolean isIsentoPontos;

    @Column(name = "isisentotaxas")
    private Boolean isIsentoTaxas;

    @Column(name = "percentualdescontopontos", precision = 19, scale = 2)
    private BigDecimal percentualDescontoPontos;

    @Column(name = "percentualdescontotaxaspensao", precision = 19, scale = 2)
    private BigDecimal percentualDescontoTaxasPensao;

    @Column(name = "percentualdescontoprimeirautilizacaopensao", precision = 19, scale = 2)
    private BigDecimal percentualDescontoPrimeiraUtilizacaoPensao;

    @Column(name = "percentualdescontoprimeirautilizacaomanutencao", precision = 19, scale = 2)
    private BigDecimal percentualDescontoPrimeiraUtilizacaoManutencao;

    @Column(name = "percentualdescontotaxasmanutencao", precision = 19, scale = 2)
    private BigDecimal percentualDescontoTaxasManutencao;

    // Método estático para criar nova faixa etária
    public static FaixaEtaria novaFaixaEtaria(String descricao, String sigla, Boolean ativo, 
            Boolean isPagante, Boolean isIsentoPontos, Boolean isIsentoTaxas) {
        
        FaixaEtaria novaFaixa = new FaixaEtaria();
        novaFaixa.setDescricao(descricao);
        novaFaixa.setSigla(sigla);
        novaFaixa.setAtivo(ativo);
        novaFaixa.setIsPagante(isPagante);
        novaFaixa.setIsIsentoPontos(isIsentoPontos);
        novaFaixa.setIsIsentoTaxas(isIsentoTaxas);
        
        return novaFaixa;
    }

    // Método para alterar dados básicos
    public void alterarDados(String novaDescricao, String novaSigla, Boolean novoAtivo) {
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricao(novaDescricao);
        }
        
        if (novaSigla != null && !novaSigla.trim().isEmpty()) {
            this.setSigla(novaSigla);
        }
        
        if (novoAtivo != null) {
            this.setAtivo(novoAtivo);
        }
    }

    // Método para configurar percentuais de desconto
    public void configurarPercentuaisDesconto(BigDecimal percentualDescontoPontos,
            BigDecimal percentualDescontoTaxasPensao, BigDecimal percentualDescontoPrimeiraUtilizacaoPensao,
            BigDecimal percentualDescontoPrimeiraUtilizacaoManutencao, BigDecimal percentualDescontoTaxasManutencao) {
        
        this.setPercentualDescontoPontos(percentualDescontoPontos);
        this.setPercentualDescontoTaxasPensao(percentualDescontoTaxasPensao);
        this.setPercentualDescontoPrimeiraUtilizacaoPensao(percentualDescontoPrimeiraUtilizacaoPensao);
        this.setPercentualDescontoPrimeiraUtilizacaoManutencao(percentualDescontoPrimeiraUtilizacaoManutencao);
        this.setPercentualDescontoTaxasManutencao(percentualDescontoTaxasManutencao);
    }

    // Método para configurar regras de pagamento
    public void configurarRegrasPagamento(Boolean isPagante, Boolean isIsentoPontos, Boolean isIsentoTaxas) {
        this.setIsPagante(isPagante);
        this.setIsIsentoPontos(isIsentoPontos);
        this.setIsIsentoTaxas(isIsentoTaxas);
    }

    // Método para configurar integração
    public void configurarIntegracao(Integer idIntegracao) {
        this.setIdIntegracao(idIntegracao);
    }

    // Método para ativar/desativar faixa etária
    public void ativar() {
        this.setAtivo(true);
    }

    public void desativar() {
        this.setAtivo(false);
    }
}
