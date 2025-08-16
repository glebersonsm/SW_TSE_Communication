package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BuscaCepBrasilApiResponse(
		@JsonProperty("cep") String cep,
		@JsonProperty("state") String uf, 
		@JsonProperty("city") String cidade,
		@JsonProperty("street") String logradouro,
		@JsonProperty("neighborhood") String bairro
) {}
