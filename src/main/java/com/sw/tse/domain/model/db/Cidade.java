package com.sw.tse.domain.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name ="cidade")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cidade {

	@Id
	@Column(name ="idcidade")
	private long id;
	@Column(name ="nome")
	private String nome;
	@Column(name ="codigoibge")
	private String codigoIbge;
	@Column(name ="idcountrystate")
	private Long idEstado;
	@Column(name ="uf")
	private String uf;
	@Column(name ="idcountry")
	private Long idPais;
}
