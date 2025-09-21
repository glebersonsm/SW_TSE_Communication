package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.model.api.response.LoginResponse;

public interface LoginService {
	LoginResponse logarOperadorCliente(String login, String password);
}
