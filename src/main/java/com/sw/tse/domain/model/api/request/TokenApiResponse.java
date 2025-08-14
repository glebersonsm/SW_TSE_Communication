package com.sw.tse.domain.model.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenApiResponse(
	    @JsonProperty("access_token") String accessToken,
	    @JsonProperty("expires_in") int expiresIn,
	    @JsonProperty("token_type") String tokenType
) {}
