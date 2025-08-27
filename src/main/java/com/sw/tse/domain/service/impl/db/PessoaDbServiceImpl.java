package com.sw.tse.domain.service.impl.db;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.db.Cidade;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.model.db.TipoEnderecoPessoa;
import com.sw.tse.domain.model.db.TipoLogradouro;
import com.sw.tse.domain.service.interfaces.CidadeService;
import com.sw.tse.domain.service.interfaces.PessoaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
public class PessoaDbServiceImpl implements PessoaService {
	
	private final PessoaConverter pessoaConverter;
	
	@Override
	public Long salvar(HospedeDto hospedeDto) {
		
		Pessoa pessoa = pessoaConverter.hospedeDtoToPessoa(hospedeDto);
		return null;
	}

}
