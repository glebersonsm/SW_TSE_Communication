package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.model.api.response.CidadeApiResponse;

public interface CidadeService {

	public CidadeApiResponse buscarPorCep(String cep);
}
