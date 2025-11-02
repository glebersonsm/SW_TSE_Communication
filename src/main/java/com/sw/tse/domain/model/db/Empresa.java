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
@Table(name = "empresa")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqempresa")
    @SequenceGenerator(name = "seqempresa", sequenceName = "seqempresa", allocationSize = 1)
    @Column(name = "idempresa")
    private Long id;

    @Column(name = "sigla", length = 10)
    private String sigla;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    // Relacionamento com Pessoa (baseado na FK idpessoa)
    @ManyToOne
    @JoinColumn(name = "idpessoa")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne
    @JoinColumn(name = "idrespadministracaocondominio")
    private Empresa empresaAdministracaoCondominio;

    // Método estático para criar nova empresa
    static Empresa novaEmpresa(String sigla, Pessoa pessoa, OperadorSistema responsavelCadastro) {
        Empresa novaEmpresa = new Empresa();
        novaEmpresa.setSigla(sigla);
        novaEmpresa.setPessoa(pessoa);
        novaEmpresa.setResponsavelCadastro(responsavelCadastro);
        
        return novaEmpresa;
    }
}