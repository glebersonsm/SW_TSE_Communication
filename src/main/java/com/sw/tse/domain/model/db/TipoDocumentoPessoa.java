package com.sw.tse.domain.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipodocumentopessoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoDocumentoPessoa {

	@Id
	@Column(name = "idtipodocumentopessoa")
	private Long id;
	@Column(name = "")
	private String decricao;
}
