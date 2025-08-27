package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.converter.TipoLogradouroConverter;
import com.sw.tse.domain.model.api.response.TipoLogradouroDto;
import com.sw.tse.domain.model.db.TipoLogradouro;
import com.sw.tse.domain.repository.TipoLogradouroRepository;
import com.sw.tse.domain.service.interfaces.TipoLogradouroService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
public class TipoLogradouroDbService implements TipoLogradouroService{
	
	private final TipoLogradouroRepository tipoLogradouroRepository;
	private final TipoLogradouroConverter tipoLogradouroConverter;

	@Override
	public List<TipoLogradouroDto> listarTiposLogradouro() {
		List<TipoLogradouro> listaTipoLogradouro = tipoLogradouroRepository.findAll();
		return tipoLogradouroConverter.toDtoList(listaTipoLogradouro);
	}

}
