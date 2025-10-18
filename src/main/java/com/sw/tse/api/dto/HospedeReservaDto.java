package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record HospedeReservaDto(
    @NotNull
    @JsonProperty("idPessoa")
    Long idPessoa,
    
    @NotNull
    @JsonProperty("idTipoHospede")
    Long idTipoHospede
) {}
