package com.sw.tse.domain.service.impl;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.client.PessoaApiClient;
import com.sw.tse.controller.model.HospedeDto;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;
import com.sw.tse.domain.service.interfaces.PessoaService;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
public class PessoaApiServiceImpl implements PessoaService {
	
	private final PessoaApiClient pessoaApiClient;
	private final TokenTseService tokenTseService;
	private final PessoaConverter pessoaConverter;
	
	@Override
	public Long salvar(HospedeDto hospedeDto) {
		
		PessoaApiRequest request = pessoaConverter.toPessoaApiHospedeDto(hospedeDto);
		Long idPessoa = hospedeDto.idHospede() == null ? 0 : hospedeDto.idHospede();
		
		log.info("Iniciando processo de salvar pessoa com ID: {}", idPessoa);
		
		try {
			String bearerToken = "Bearer " + tokenTseService.gerarToken();
			
			if(request.cpfCnpj() != null) {
				 Optional<PessoaCpfApiResponse> optionalPessoaCpf = buscarPorCpf(bearerToken, request.cpfCnpj());
				 if(optionalPessoaCpf.isPresent()) {
					 return optionalPessoaCpf.get().idPessoa();
				 }
			}
		
			log.info("Enviando requisição para salvar pessoa");
			
			Long idPessoaSalva = Long.valueOf(pessoaApiClient.salvarPessoa(idPessoa, bearerToken, request));
		
			return idPessoaSalva;
		 } catch (FeignException e) {
	            log.error("Erro ao chamar a API de Pessoas. Status: {}, Corpo: {}", e.status(), e.contentUTF8(), e);
	            if (e.status() == 400) {
	                throw new ApiTseException("Dados inválidos enviados para a API de Pessoas: " + e.contentUTF8());
	            }
	            if (e.status() == 401 || e.status() == 403) {
	                 throw new ApiTseException("Falha de autenticação/autorização com a API do TSE.");
	            }
	            throw new ApiTseException("Erro de comunicação com a API de Pessoas.", e);
	    }
	}
	
	
	private Optional<PessoaCpfApiResponse> buscarPorCpf(String token, String cpf) {
		try {			
			PessoaCpfApiResponse pessoaCpf = pessoaApiClient.buscarPorCpf(cpf, token);
			return Optional.of(pessoaCpf);
		} catch (FeignException e) {
			if(e.status() == 400) {
				return Optional.empty();
			}
			throw new ApiTseException(e.contentUTF8());
			
		}
	}

	
}
