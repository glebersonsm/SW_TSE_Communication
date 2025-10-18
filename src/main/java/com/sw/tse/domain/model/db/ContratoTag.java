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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contratotstag")
@Getter
@Setter
@NoArgsConstructor
public class ContratoTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcontratotstag")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idcontrato")
    private Contrato contrato;
    
    @ManyToOne
    @JoinColumn(name = "idtipolabel")
    private ContratoTipoTag tipoTag;
    
    @Column(name = "descricao", length = 512)
    private String descricao;
    
    @Column(name = "ativo")
    private Boolean ativo;
    
    @Column(name = "datainativado")
    private LocalDateTime dataInativado;
    
    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;
    
    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;
    
    @ManyToOne
    @JoinColumn(name = "idrespcadastro")
    private OperadorSistema responsavelCadastro;
    
    @ManyToOne
    @JoinColumn(name = "idrespalteracao")
    private OperadorSistema responsavelAlteracao;
    
    @ManyToOne
    @JoinColumn(name = "idrespinativar")
    private OperadorSistema responsavelInativar;
    
    @ManyToOne
    @JoinColumn(name = "idtenant")
    private Empresa empresa;
    
    @ManyToOne
    @JoinColumn(name = "idcontratotstagorigemadm")
    private ContratoTag tagOrigemAdm;
}
