package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.api.response.ContratoPessoaApiResponse;

public interface ContratoService {
	List<ContratoPessoaApiResponse> buscarContratoPorCPF(String cpf);
}
