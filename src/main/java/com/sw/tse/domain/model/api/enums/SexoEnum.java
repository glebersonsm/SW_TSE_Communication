package com.sw.tse.domain.model.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SexoEnum {

	FEMININO(1, "Feminino"),
    MASCULINO(0, "Masculino");

    private final int codigo;
    private final String descricao;

    SexoEnum(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    @JsonValue
    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
