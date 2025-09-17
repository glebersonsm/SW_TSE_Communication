package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TipoDocumentoPessoaApiResponse(
		@JsonProperty("idtipodocumentopessoa") Long id,
		@JsonProperty("descricao") String descricao,
		@JsonProperty("sysid") String sydId
) {}
