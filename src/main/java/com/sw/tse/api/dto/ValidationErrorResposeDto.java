package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ValidationErrorResposeDto(
		  @JsonProperty("campo") String field,
		  @JsonProperty("valorRejeitado") Object rejectedValue,
		  @JsonProperty("mensagem") String message
		) {}
