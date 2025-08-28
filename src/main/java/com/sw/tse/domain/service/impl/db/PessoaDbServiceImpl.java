package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.service.interfaces.PessoaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
public class PessoaDbServiceImpl implements PessoaService {
	
	private final PessoaConverter pessoaConverter;
	private final PessoaRepository pessoaRepository;
	
	@Override
	public Long salvar(HospedeDto hospedeDto) {
		Pessoa pessoa = new Pessoa();
		
		if()
		
		if(StringUtils.hasText(hospedeDto.cpf())) {
			String cpf = com.sw.tse.core.util.StringUtil.removeMascaraCpf(hospedeDto.cpf());
			List<Pessoa> listaPessoa = pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(cpf);
			if(!listaPessoa.isEmpty()) {
				pessoa = listaPessoa.get(0);
			}
			
		}
		
		pessoa = pessoaConverter.hospedeDtoToPessoa(hospedeDto,pessoa);
		return null;
	}

}
