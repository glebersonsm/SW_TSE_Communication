package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipoutilizacaocontrato")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TipoUtilizacaoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtipoutilizacaocontrato", nullable = false)
    private Long id;

    @Column(name = "sigla", length = 10)
    private String sigla;

    @Column(name = "descricao", length = 30)
    private String descricao;

    @CreationTimestamp
    @Column(name = "datacadastro")
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    // Método estático para criar novo tipo de utilização de contrato
    public static TipoUtilizacaoContrato novoTipoUtilizacaoContrato(String sigla, String descricao) {
        TipoUtilizacaoContrato novoTipo = new TipoUtilizacaoContrato();
        novoTipo.setSigla(sigla);
        novoTipo.setDescricao(descricao);
        
        return novoTipo;
    }

    // Método para alterar dados
    public void alterarDados(String novaSigla, String novaDescricao) {
        if (novaSigla != null && !novaSigla.trim().isEmpty()) {
            this.setSigla(novaSigla);
        }
        
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricao(novaDescricao);
        }
    }
}
