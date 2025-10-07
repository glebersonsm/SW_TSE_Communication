package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.api.dto.OperadorSistemaRequestDto;
import com.sw.tse.domain.model.api.response.BuscaOperadorSistemPessoaResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaCriadoApiResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaListaApiResponse;
import com.sw.tse.domain.model.db.OperadorSistema;

public interface OperadorSistemaService {
	List<OperadorSistemaListaApiResponse> listarTodos();
	OperadorSistemaListaApiResponse buscarPorId(Long idOperadorSistema);
	OperadorSistema operadorSistemaPadraoCadastro();
	OperadorSistemaCriadoApiResponse criarOperadorSistema(OperadorSistemaRequestDto requestDto);
	BuscaOperadorSistemPessoaResponse buscarPorIdPessoa(Long idPessoa);
	BuscaOperadorSistemPessoaResponse buscarPorLogin(String login);
}
