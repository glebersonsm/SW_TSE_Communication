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
@Table(name = "contratotstipotaggrupotag")
@Getter
@Setter
@NoArgsConstructor
public class ContratoTipoTagGrupoTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcontratotstipotaggrupotag")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "idcontratotstipotag")
    private ContratoTipoTag tipoTag;
    
    @ManyToOne
    @JoinColumn(name = "idgrupotag")
    private ConvencaoSistema grupoTag;
    
    @Column(name = "statusnegociacao")
    private Integer statusNegociacao;
    
    @Column(name = "deletado", nullable = false)
    private Boolean deletado = false;
    
    @Column(name = "datahoradelete")
    private LocalDateTime dataHoraDelete;
    
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
    @JoinColumn(name = "idrespdelete")
    private OperadorSistema responsavelDelete;
}
