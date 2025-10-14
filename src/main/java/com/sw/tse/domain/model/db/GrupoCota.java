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
@Table(name = "grupocota")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GrupoCota {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqgrupocota")
    @SequenceGenerator(name = "seqgrupocota", sequenceName = "seqgrupocota", allocationSize = 1)
    @Column(name = "idgrupocota")
    private Long id;

    @Column(name = "descricao", length = 60)
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

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @Column(name = "idimportacao", length = 100)
    private String idImportacao;

    @Column(name = "dataperiodoinicial")
    private LocalDateTime dataPeriodoInicial;

    @Column(name = "permitirsomenteperiodosvinculadosparaescolha")
    private Boolean permitirSomentePeriodosVinculadosParaEscolha;

    // Método estático para criar novo grupo de cota
    static GrupoCota novoGrupoCota(String descricao, Empresa empresa, 
            OperadorSistema responsavelCadastro, LocalDateTime dataPeriodoInicial,
            Boolean permitirSomentePeriodosVinculadosParaEscolha) {
        
        GrupoCota novoGrupo = new GrupoCota();
        novoGrupo.setDescricao(descricao);
        novoGrupo.setEmpresa(empresa);
        novoGrupo.setResponsavelCadastro(responsavelCadastro);
        novoGrupo.setDataPeriodoInicial(dataPeriodoInicial);
        novoGrupo.setPermitirSomentePeriodosVinculadosParaEscolha(permitirSomentePeriodosVinculadosParaEscolha);
        
        return novoGrupo;
    }
}
