package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.model.api.response.TokenApiResponse;

public interface TokenTseService {
	public String gerarToken();
	public TokenApiResponse gerarTokenClient(String userName, String password);
}
