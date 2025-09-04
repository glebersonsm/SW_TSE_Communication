package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.core.config.CadastroOperadorSistemaPropertiesCustom;
import com.sw.tse.domain.converter.OperadorSistemaConverter;
import com.sw.tse.domain.expection.OperadorSistemaNotFoundException;
import com.sw.tse.domain.expection.ValorPadraoNaoConfiguradoException;
import com.sw.tse.domain.model.api.response.OperadorSistemaDto;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.repository.OperadorSistemaRepository;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;

import lombok.RequiredArgsConstructor;


@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
public class OperadorSistemaDbServiceImpl implements OperadorSistemaService {
	
	private final OperadorSistemaRepository operadorSistemaRepository;
	private final OperadorSistemaConverter operadorSistemaConverter;
	
	private final CadastroOperadorSistemaPropertiesCustom cadastroOperadorSistemaPropertiesCustom;

	@Override
	public List<OperadorSistemaDto> listarTodos() {
		List<OperadorSistema> listaOperadoresSistema = operadorSistemaRepository.findAll();
		return operadorSistemaConverter.toDtoList(listaOperadoresSistema);
	}

	@Override
	public OperadorSistemaDto buscarPorId(Long idOperadorSistema) {
		OperadorSistema operadorSistema = operadorSistemaRepository.findById(idOperadorSistema)
				.orElseThrow(() -> new OperadorSistemaNotFoundException(String.format("Não existe operador sistema com id %d", idOperadorSistema)));
		return operadorSistemaConverter.toDto(operadorSistema);
	}
	
	
	@Override
	public OperadorSistema operadorSistemaPadraoCadastro() {
		if(cadastroOperadorSistemaPropertiesCustom.getOperador() == null || 
				cadastroOperadorSistemaPropertiesCustom.getOperador().equals(0L)) {
			throw new ValorPadraoNaoConfiguradoException("Usuário padrão para cadastro não configurado");
		}
		
		return operadorSistemaRepository.findById(cadastroOperadorSistemaPropertiesCustom.getOperador())
				.orElseThrow(() -> new OperadorSistemaNotFoundException(String.format("Não existe operador sistema com id %d", cadastroOperadorSistemaPropertiesCustom.getOperador())));
	}

	
}
