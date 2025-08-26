package com.sw.tse.domain.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name ="tipoenderecopessoa")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoEnderecoPessoa {

	@Id
	@Column(name ="idtipoenderecopessoa")
	private Long id;
	@Column(name ="descricao")
	private String decricao;
	@Column(name ="sysid")
	private String sysId;
	
}
