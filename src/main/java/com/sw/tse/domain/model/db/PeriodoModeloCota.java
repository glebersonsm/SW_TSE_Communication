package com.sw.tse.domain.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "periodosmodelocota")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PeriodoModeloCota {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqperiodomodelocota")
    @SequenceGenerator(name = "seqperiodomodelocota", sequenceName = "seqperiodomodelocota", allocationSize = 1)
    @Column(name = "idperiodomodelocota")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idperiodoutilizacao")
    private PeriodoUtilizacao periodoUtilizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idmodelocota")
    private ModeloCota modeloCota;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "idgeracaoperiodocota")
    private Integer idGeracaoPeriodoCota;

    @Column(name = "dataprimeirautilizacao")
    private LocalDateTime dataPrimeiraUtilizacao;

    @Column(name = "semanames")
    private Integer semanaMes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idunidadehoteleira")
    private UnidadeHoteleira unidadeHoteleira;

    @Column(name = "datainicial")
    private LocalDateTime dataInicial;

    @Column(name = "rotativo")
    private Boolean rotativo;

    @Column(name = "periodomanual")
    private Boolean periodoManual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcontrato")
    private Contrato contrato;

    @Column(name = "deletado")
    private Boolean deletado;

    @Column(name = "datahoradelete")
    private LocalDateTime dataHoraDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespdelete")
    private OperadorSistema responsavelDelete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcotauh")
    private CotaUh cotaUh;

    @Column(name = "statusintercambio")
    private Integer statusIntercambio = 0;

    @Column(name = "idintegracao", length = 128)
    private String idIntegracao;

    // Método estático para criar novo período modelo cota
    public static PeriodoModeloCota novoPeriodoModeloCota(Contrato contrato, 
            PeriodoUtilizacao periodoUtilizacao, OperadorSistema responsavelCadastro) {
        
        PeriodoModeloCota novoPeriodoModelo = new PeriodoModeloCota();
        
        // Parâmetros diretos
        novoPeriodoModelo.setContrato(contrato);
        novoPeriodoModelo.setPeriodoUtilizacao(periodoUtilizacao);
        novoPeriodoModelo.setResponsavelCadastro(responsavelCadastro);
        
        // Campos derivados do contrato
        novoPeriodoModelo.setModeloCota(contrato.getCotaUh().getModeloCota());
        novoPeriodoModelo.setEmpresa(contrato.getEmpresa());
        novoPeriodoModelo.setUnidadeHoteleira(contrato.getCotaUh().getUnidadeHoteleira());
        
        // Campo dataInicial construído a partir do período de utilização
        if (periodoUtilizacao.obterDataInicio() != null) {
            novoPeriodoModelo.setDataInicial(periodoUtilizacao.obterDataInicio().atStartOfDay());
        }
        
        // Valores padrão
        novoPeriodoModelo.setSemanaMes(0);
        novoPeriodoModelo.setPeriodoManual(false);
        novoPeriodoModelo.setDeletado(false);
        
        return novoPeriodoModelo;
    }
    
    public void deletarPeriodo(OperadorSistema responsavel) {
        LocalDateTime agora = LocalDateTime.now().withNano(0);
        this.setDeletado(true);
        this.setDataHoraDelete(agora);
        this.setDataAlteracao(agora);
        this.setResponsavelDelete(responsavel);
        this.setResponsavelAlteracao(responsavel);
    }
}
