package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OperadorSistemaListaApiResponse(
		@JsonProperty("Value") Long id,
		@JsonProperty("Login") String login,
		@JsonProperty("Nome") String nome,
		@JsonProperty("Habilitado") boolean habilitado
	){}
