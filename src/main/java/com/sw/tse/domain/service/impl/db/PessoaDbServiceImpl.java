package com.sw.tse.domain.service.impl.db;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.Pessoa;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;
import com.sw.tse.domain.service.interfaces.PessoaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@RequiredArgsConstructor
@Service
public class PessoaDbServiceImpl implements PessoaService {
	
	private final PessoaConverter pessoaConverter;
	private final PessoaRepository pessoaRepository;
	
	private final OperadorSistemaService operadorSistemaService;
	
	

	@Transactional
	@Override
	public Long salvar(HospedeDto hospedeDto) {
		Pessoa pessoa = new Pessoa();
		
		if(StringUtils.hasText(hospedeDto.numeroDocumento())) {
			String numeroDocumento = StringUtil.removeMascaraCpf(hospedeDto.numeroDocumento());
			List<Pessoa> listaPessoa = new ArrayList<>();
			
			if(hospedeDto.tipoDocumento() == null || hospedeDto.tipoDocumento().equals("CPF")) {
				listaPessoa = pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(numeroDocumento);
			} else {
				
			}
			
			if(!listaPessoa.isEmpty()) {
				pessoa = listaPessoa.get(0);
			}
			
		}
		
		OperadorSistema responsavelCadastro = operadorSistemaService.operadorSistemaPadraoCadastro();
		
		pessoa = pessoaConverter.hospedeDtoToPessoa(hospedeDto, pessoa , responsavelCadastro);
		
		pessoaRepository.save(pessoa);
		
		return pessoa.getIdPessoa();
	}


}
