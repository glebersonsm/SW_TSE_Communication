package com.sw.tse.domain.model.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoTelefone {
	CELULAR(0),
    REESIDENCIAL(1),
    COMERCIAL(2),
    OUTROS(3);

    private final int codigo;

    TipoTelefone(int codigo) {
        this.codigo = codigo;
    }
    
    @JsonValue
    public int getCodigo() {
        return codigo;
    }
}
