package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.model.api.response.TokenApiResponse;

public interface LoginService {
	TokenApiResponse logar(String login, String password);
}
