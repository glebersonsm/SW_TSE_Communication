package com.sw.tse.domain.model.db;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sw.tse.core.util.ModeloUtilizacaoEnumConverter;
import com.sw.tse.domain.model.api.enums.ModeloUtilizacaoEnum;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "periodoutilizacao")
@Setter(value = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PeriodoUtilizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqperiodoutilizacao")
    @SequenceGenerator(name = "seqperiodoutilizacao", sequenceName = "seqperiodoutilizacao", allocationSize = 1)
    @Column(name = "idperiodoutilizacao")
    private Long id;

    @Column(name = "descricaoperiodo", length = 120)
    private String descricaoPeriodo;

    @ManyToOne
    @JoinColumn(name = "idtipoperiodoutilizacao")
    private TipoPeriodoUtilizacao tipoPeriodoUtilizacao;

    @Column(name = "diainicio")
    private Integer diaInicio;

    @Column(name = "mesinicio")
    private Integer mesInicio;

    @Column(name = "anoinicio")
    private Integer anoInicio;

    @Column(name = "diafim")
    private Integer diaFim;

    @Column(name = "mesfim")
    private Integer mesFim;

    @Column(name = "anofim")
    private Integer anoFim;

    @Column(name = "habilitado")
    private Boolean habilitado;

    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;

    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;

    @Column(name = "idgeracaoperiodocota")
    private Integer idGeracaoPeriodoCota;

    @Convert(converter = ModeloUtilizacaoEnumConverter.class)
    @Column(name = "modeloutilizacao")
    private ModeloUtilizacaoEnum modeloUtilizacao;

    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;

    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;


    public void definirDataInicio(LocalDate dataInicio) {
        if (dataInicio != null) {
            this.diaInicio = dataInicio.getDayOfMonth();
            this.mesInicio = dataInicio.getMonthValue();
            this.anoInicio = dataInicio.getYear();
        }
    }

    public void definirDataFim(LocalDate dataFim) {
        if (dataFim != null) {
            this.diaFim = dataFim.getDayOfMonth();
            this.mesFim = dataFim.getMonthValue();
            this.anoFim = dataFim.getYear();
        }
    }

    public LocalDate obterDataInicio() {
        if (anoInicio != null && mesInicio != null && diaInicio != null) {
            return LocalDate.of(anoInicio, mesInicio, diaInicio);
        }
        return null;
    }

    public LocalDate obterDataFim() {
        if (anoFim != null && mesFim != null && diaFim != null) {
            return LocalDate.of(anoFim, mesFim, diaFim);
        }
        return null;
    }

    public void alterarPeriodo(LocalDate novaDataInicio, LocalDate novaDataFim, 
            TipoPeriodoUtilizacao novoTipoPeriodoUtilizacao, String novaDescricao, 
            OperadorSistema responsavelAlteracao) {
        
        if (novaDataInicio != null) {
            this.definirDataInicio(novaDataInicio);
        }
        
        if (novaDataFim != null) {
            this.definirDataFim(novaDataFim);
        }
        
        if (novoTipoPeriodoUtilizacao != null) {
            this.setTipoPeriodoUtilizacao(novoTipoPeriodoUtilizacao);
        }
        
        if (novaDescricao != null && !novaDescricao.trim().isEmpty()) {
            this.setDescricaoPeriodo(novaDescricao);
        }
        
        this.setResponsavelAlteracao(responsavelAlteracao);
    }
    
    public void bloquearPeriodo(OperadorSistema responsavelAlteracao) {
    	this.setHabilitado(false);
    	this.setResponsavelAlteracao(responsavelAlteracao);
    }
    
    public void desbloquearPeriodo(OperadorSistema responsavelAlteracao) {
    	this.setHabilitado(true);
    	this.setResponsavelAlteracao(responsavelAlteracao);
    }
    

    static PeriodoUtilizacao novoPeriodoUtilizacao(String descricaoPeriodo, TipoPeriodoUtilizacao tipoPeriodoUtilizacao,
            LocalDate dataInicio, LocalDate dataFim, Boolean habilitado, Empresa empresa,
            Integer idGeracaoPeriodoCota, ModeloUtilizacaoEnum modeloUtilizacao, OperadorSistema responsavelCadastro) {
        
        PeriodoUtilizacao novoPeriodo = new PeriodoUtilizacao();
        novoPeriodo.setDescricaoPeriodo(descricaoPeriodo);
        novoPeriodo.setTipoPeriodoUtilizacao(tipoPeriodoUtilizacao);
        novoPeriodo.definirDataInicio(dataInicio);
        novoPeriodo.definirDataFim(dataFim);
        novoPeriodo.setHabilitado(habilitado);
        novoPeriodo.setEmpresa(empresa);
        novoPeriodo.setIdGeracaoPeriodoCota(idGeracaoPeriodoCota);
        novoPeriodo.setModeloUtilizacao(modeloUtilizacao);
        novoPeriodo.setResponsavelCadastro(responsavelCadastro);
        
        return novoPeriodo;
    }
}