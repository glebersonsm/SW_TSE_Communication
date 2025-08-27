package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.model.api.response.CidadeDto;

public interface CidadeService {

	public CidadeDto buscarPorCep(String cep);
}
