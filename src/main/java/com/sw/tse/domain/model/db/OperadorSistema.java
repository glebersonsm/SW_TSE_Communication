package com.sw.tse.domain.model.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "operadorsistema")
@Entity()
public class OperadorSistema {

	@Id
	@Column(name ="idoperadorsistema")
	private Long id;
	@Column(name = "nome")
	private String nome;
	@Column(name ="login")
	private String login;
	@Column(name = "habilitado")
	private boolean habilitado;
	
	@ManyToOne()
    @JoinColumn(name = "idpessoa")
    private Pessoa pessoa;
}
