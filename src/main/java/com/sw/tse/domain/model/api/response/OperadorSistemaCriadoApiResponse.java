package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record OperadorSistemaCriadoApiResponse(
	    @JsonProperty("IdOperador") Long idOperador,
	    @JsonProperty("NomeOperador") String nomeOperador,
	    @JsonProperty("Login") String login,
	    @JsonProperty("IdFuncionario") Long idFuncionario
	) {}