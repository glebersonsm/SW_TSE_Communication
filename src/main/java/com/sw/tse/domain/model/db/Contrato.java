package com.sw.tse.domain.model.db;

import java.math.BigDecimal;

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
@Table(name = "contrato")
@Setter(value =  AccessLevel.PRIVATE)
@Getter()
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Contrato {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqcontrato")
    @SequenceGenerator(name = "seqcontrato", sequenceName = "seqcontrato", allocationSize = 1)
 	@Column(name = "idcontrato")
	private Long id;
	@ManyToOne()
    @JoinColumn(name = "idpessoacessionario")
	private Pessoa pessoaCessionario;
	@ManyToOne
	@JoinColumn(name = "idpessoacocessionario")
	private Pessoa pessaoCocessionario;
	@Column(name ="numerocontrato")
	private String numeroContrato;
	@Column(name = "statuscontrato")
	private String status;
	@Column(name = "valornegociado")
	private BigDecimal valorNegociado;
    @ManyToOne
    @JoinColumn(name = "idtenant")
	private Empresa empresa;
	
	@ManyToOne()
    @JoinColumn(name = "idcotaadquirida")
	private CotaUh cotaUh;
	
	@Column(name = "idcontratoorigemadm")
	private Long idContratoOrigemAdm;
	
}
