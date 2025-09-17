package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.api.dto.OperadorSistemaRequestDto;
import com.sw.tse.domain.model.api.response.OperadorSistemaApiResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaListaApiResponse;
import com.sw.tse.domain.model.db.OperadorSistema;

public interface OperadorSistemaService {
	public List<OperadorSistemaListaApiResponse> listarTodos();
	public OperadorSistemaListaApiResponse buscarPorId(Long idOperadorSistema);
	public OperadorSistema operadorSistemaPadraoCadastro();
	public OperadorSistemaApiResponse criarOperadorSistema(OperadorSistemaRequestDto requestDto);
}
