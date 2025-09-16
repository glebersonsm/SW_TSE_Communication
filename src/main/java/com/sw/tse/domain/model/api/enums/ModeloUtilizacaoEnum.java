package com.sw.tse.domain.model.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModeloUtilizacaoEnum {
    COTA(0, "Cota"),
    PONTOS(1, "Pontos");

    private final int codigo;
    private final String descricao;

    ModeloUtilizacaoEnum(int codigo, String descricao) {
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

    public static ModeloUtilizacaoEnum fromCodigo(int codigo) {
        for (ModeloUtilizacaoEnum modelo : values()) {
            if (modelo.codigo == codigo) {
                return modelo;
            }
        }
        throw new IllegalArgumentException("Código inválido para ModeloUtilizacaoEnum: " + codigo);
    }
}