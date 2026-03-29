package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlterarSenhaServiceRequestDto(
    @JsonProperty("SenhaAtual") String senhaAtual,
    @JsonProperty("NovaSenha") String novaSenha
) {
}
