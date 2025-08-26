package com.sw.tse.domain.model.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

public enum TipoPessoaEnum {

	FISICA(0, "PessoaDbServiceImpl física"),
	JURIDICA(1, "PessoaDbServiceImpl jurídica");
	

    private final int codigo;
    @Getter
    private final String descricao;
	
	TipoPessoaEnum(int codigo, String descricao){
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	@JsonValue
	public int getCodigo() {
		return codigo;
	}
	
}
