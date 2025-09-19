package com.sw.tse.domain.service.impl.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.core.util.StringUtil;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
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



	@Override
	public Optional<PessoaCpfApiResponse> buscarPorCpf(String cpf) {
		Pessoa pessoa = 
				pessoaRepository.findFirstByCpfCnpjOrderByIdPessoa(cpf).stream().findFirst().orElse(new Pessoa());
		
		if(pessoa.getIdPessoa() == null) {
			return Optional.empty();
		}
		
		return  Optional.of(pessoaConverter.toPessoaCpfApiResponse(pessoa));
	}
	
	
	

}
