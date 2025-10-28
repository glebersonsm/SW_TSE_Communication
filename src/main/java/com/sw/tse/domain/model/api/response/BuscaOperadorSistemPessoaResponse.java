package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BuscaOperadorSistemPessoaResponse(
		@JsonProperty("idoperadorsistema") Long idOperador,
		@JsonProperty("idpessoa") Long idPessoa,
		@JsonProperty("email") String email,
		@JsonProperty("nome") String nome,
		@JsonProperty("login") String login,
		@JsonProperty("habilitado") boolean habilitado
		
) {}
