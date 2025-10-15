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
@Table(name = "tipohospede")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TipoHospede {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqtipohospede")
    @SequenceGenerator(name = "seqtipohospede", sequenceName = "seqtipohospede", allocationSize = 1)
    @Column(name = "idtipohospede")
    private Long id;

    @Column(name = "descricao", length = 250)
    private String descricao;

    @Column(name = "idintegracao", length = 250)
    private String idIntegracao;

    @CreationTimestamp
    @Column(name = "datacadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro", nullable = false)
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne
    @JoinColumn(name = "idtenant", nullable = false)
    private Empresa empresa;

    // Método estático para criar novo tipo de hóspede
    public static TipoHospede novoTipoHospede(String descricao, String idIntegracao, 
            OperadorSistema responsavelCadastro, Empresa empresa) {
        
        TipoHospede novoTipo = new TipoHospede();
        novoTipo.setDescricao(descricao);
        novoTipo.setIdIntegracao(idIntegracao);
        novoTipo.setResponsavelCadastro(responsavelCadastro);
        novoTipo.setEmpresa(empresa);
        
        return novoTipo;
    }

    // Método para alterar dados
    public void alterarDados(String novaDescricao, String novoIdIntegracao, OperadorSistema responsavelAlteracao) {
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricao(novaDescricao);
        }
        
        if (novoIdIntegracao != null && !novoIdIntegracao.trim().isEmpty()) {
            this.setIdIntegracao(novoIdIntegracao);
        }
        
        this.setResponsavelAlteracao(responsavelAlteracao);
    }

    // Método para configurar integração
    public void configurarIntegracao(String idIntegracao, OperadorSistema responsavelAlteracao) {
        this.setIdIntegracao(idIntegracao);
        this.setResponsavelAlteracao(responsavelAlteracao);
    }
}
