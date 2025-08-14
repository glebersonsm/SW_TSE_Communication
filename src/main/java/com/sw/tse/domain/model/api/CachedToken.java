package com.sw.tse.domain.model.api;

import java.time.Instant;

public record CachedToken(String accessToken, Instant expirationTime) {
	
	public boolean isValido() {
		return Instant.now().isBefore(expirationTime.minusSeconds(30));
	}
	
	
}
