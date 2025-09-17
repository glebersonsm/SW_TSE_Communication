package com.sw.tse.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OperadorSistemaRequestDto(
    @JsonProperty("Login") String login,
    @JsonProperty("TipoOperador") Integer tipoOperador,
    @JsonProperty("Email") String email,
    @JsonProperty("ListaIdEmpresasPermitidas") List<Long> listaIdEmpresasPermitidas,
    @JsonProperty("IdPessoaVincular") Long idPessoaVincular
) {}