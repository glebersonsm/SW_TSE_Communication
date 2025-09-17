package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TipoLogradouroApiResponse(
		@JsonProperty("Id") Long id,
		@JsonProperty("Descricao") String descricao) {}
