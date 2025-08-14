package com.sw.tse.domain.model.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EnderecoEmailDto(
		@JsonProperty("IdEndereco") Long idEndereco,
	    @JsonProperty("Email") String email,
	    @JsonProperty("DescricaoObs") String descricaoObs
){}
