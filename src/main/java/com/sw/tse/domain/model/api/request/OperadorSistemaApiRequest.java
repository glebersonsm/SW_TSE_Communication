package com.sw.tse.domain.model.api.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OperadorSistemaApiRequest(
	    @JsonProperty("Login") String login,
	    @JsonProperty("TipoOperador") Integer tipoOperador,
	    @JsonProperty("Email") String email,
	    @JsonProperty("ListaIdEmpresasPermitidas") List<Long> listaIdEmpresasPermitidas,
	    @JsonProperty("IdPessoaVincular") Long idPessoaVincular
	) {}
