package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.api.response.OperadorSistemaDto;
import com.sw.tse.domain.model.db.OperadorSistema;

public interface OperadorSistemaService {
	public List<OperadorSistemaDto> listarTodos();
	public OperadorSistemaDto buscarPorId(Long idOperadorSistema);
	public OperadorSistema operadorSistemaPadraoCadastro();
}
