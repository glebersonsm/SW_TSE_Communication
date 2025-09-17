package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.converter.TipoDocumentoPessoaConverter;
import com.sw.tse.domain.model.api.response.TipoDocumentoPessoaApiResponse;
import com.sw.tse.domain.model.db.TipoDocumentoPessoa;
import com.sw.tse.domain.repository.TipoDocumentoPessoaRepository;
import com.sw.tse.domain.service.interfaces.TipoDocumentoPessoaService;

import lombok.RequiredArgsConstructor;


@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@Service
@RequiredArgsConstructor
public class TipoDocumentoPessoaDbService implements TipoDocumentoPessoaService {
	
	private final TipoDocumentoPessoaConverter tipoDocumentoPessoaConverter;
	private final TipoDocumentoPessoaRepository tipoDocumentoPessoaRepository;
	
	
	@Override
	public List<TipoDocumentoPessoaApiResponse> listarTiposDocumento() {
		List<TipoDocumentoPessoa> listaTipoDocumentoPessoa = tipoDocumentoPessoaRepository.findAll();
		return tipoDocumentoPessoaConverter.toDtoList(listaTipoDocumentoPessoa);
	}

	
}
