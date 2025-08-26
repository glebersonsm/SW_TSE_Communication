package com.sw.tse.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TipoEnderecoDto (
		@JsonProperty("Id") Long id,
		@JsonProperty("Descricao") String descricao
) {}
