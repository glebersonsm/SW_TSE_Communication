package com.sw.tse.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Grupo de cota (CotaUh) para uso em configuração de tags de visualização no Portal.
 * ID é o idCotaUh utilizado no contexto do usuário e na tag Grupo Cota.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoCotaDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;
}
