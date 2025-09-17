package com.sw.tse.domain.model.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenApiResponse(
	    @JsonProperty("access_token") String accessToken,
	    @JsonProperty("expires_in") int expiresIn,
	    @JsonProperty("token_type") String tokenType,
	    @JsonProperty("userId") Long idUsuario,
	    @JsonProperty("error") String erro,
	    @JsonProperty("error_description") String descricaoErro
) {
	
	public boolean isError() {
        return this.erro != null && !this.erro.isBlank();
    }
}
