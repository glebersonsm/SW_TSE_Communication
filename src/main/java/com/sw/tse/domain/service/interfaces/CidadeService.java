package com.sw.tse.domain.service.interfaces;

import com.sw.tse.controller.model.CidadeDto;

public interface CidadeService {

	public CidadeDto buscarPorCep(String cep);
}
