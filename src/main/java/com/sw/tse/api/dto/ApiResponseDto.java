package com.sw.tse.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponseDto<T>(
		@JsonProperty("status") Integer status,
	    @JsonProperty("success") boolean success,
	    @JsonProperty("data") T data,
	    @JsonProperty("message") String message
	) {}
