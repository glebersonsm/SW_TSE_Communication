package com.sw.tse.domain.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name ="tipologradouro")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoLogradouro {

	@Id
	@Column(name ="idtipologradouro")
	private Long id;
	@Column(name ="descricao")
	private String descricao;
	@Column(name ="descricaoabreviada")
	private String descricaoAbreviada;
}
