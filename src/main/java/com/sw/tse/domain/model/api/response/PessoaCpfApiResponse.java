package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PessoaCpfApiResponse(
		@JsonProperty("IdPessoa") Long idPessoa,
		@JsonProperty("NomePessoa") String nome,
		@JsonProperty("Email") String email
) {}
