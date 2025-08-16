package com.sw.tse.domain.model.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoValorRelatorioCustomizado {

    STRING("string"),
    INTETEIRO("int");

    private final String codigo;

    TipoValorRelatorioCustomizado(String codigo) {
        this.codigo = codigo;
    }
    
    @JsonValue
    public String getCodigo() {
        return codigo;
    }
}
