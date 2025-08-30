package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TipoDocumentoPessoaDto(
		@JsonProperty("idtipodocumentopessoa") Long id,
		@JsonProperty("descricao") String descricao,
		@JsonProperty("sysid") String sydId
) {}
