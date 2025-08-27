package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.converter.TipoEnderecoConverter;
import com.sw.tse.domain.model.api.response.TipoEnderecoDto;
import com.sw.tse.domain.model.db.TipoEnderecoPessoa;
import com.sw.tse.domain.repository.TipoEnderecoRepository;
import com.sw.tse.domain.service.interfaces.TipoEnderecoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
@Service
public class TipoEnderecoDbServiceImpl implements TipoEnderecoService {
	
	private final TipoEnderecoRepository tipoEnderecoRepository;
	private final TipoEnderecoConverter tipoEnderecoConverter;

	@Override
	public List<TipoEnderecoDto> listarTiposEndereco() {
		List<TipoEnderecoPessoa> listaEnderecos = tipoEnderecoRepository.findAll();
		return tipoEnderecoConverter.toDtoList(listaEnderecos);
	}

}
