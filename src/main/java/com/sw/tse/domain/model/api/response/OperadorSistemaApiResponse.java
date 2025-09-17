package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OperadorSistemaApiResponse(
	    @JsonProperty("IdOperador") Long idOperador,
	    @JsonProperty("NomeOperador") String nomeOperador,
	    @JsonProperty("Login") String login,
	    @JsonProperty("IdFuncionario") Long idFuncionario
	) {}