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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "convencaosistema")
@Getter
@Setter
@NoArgsConstructor
public class ConvencaoSistema {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqconvencaosistema")
    @SequenceGenerator(name = "seqconvencaosistema", sequenceName = "seqconvencaosistema", allocationSize = 1)
    @Column(name = "idconvencaosistema")
    private Long id;
    
    @Column(name = "sysid", length = 250)
    private String sysId;
    
    @Column(name = "grupo", length = 250)
    private String grupo;
    
    @ManyToOne
    @JoinColumn(name = "idconvencaosistemapai")
    private ConvencaoSistema convencaoSistemaPai;
    
    @Column(name = "descricao", length = 250)
    private String descricao;
    
    @CreationTimestamp
    @Column(name = "datacadastro", updatable = false)
    private LocalDateTime dataCadastro;
    
    @UpdateTimestamp
    @Column(name = "dataalteracao")
    private LocalDateTime dataAlteracao;
}
