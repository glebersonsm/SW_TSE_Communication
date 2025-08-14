package com.sw.tse.domain.service.impl;

import org.springframework.stereotype.Service;

import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.service.interfaces.PessoaApiClient;
import com.sw.tse.domain.service.interfaces.TokenTseService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PessoaApiService {

	private final PessoaApiClient pessoaApiClient;
	private final TokenTseService tokenTseService;
	
	public String salvarPessoa(Long idPessoa, PessoaApiRequest request) {
		
		log.info("Iniciando processo de salvar pessoa com ID: {}", idPessoa);
		
		try {
			String accessToken = tokenTseService.gerarToken();
		
			String bearerToken = "Bearer " + accessToken;
		
			log.info("Enviando requisição para salvar pessoa");
		
			String idPessoaSalva = pessoaApiClient.salvarPessoa(idPessoa, bearerToken, request);
		
			return idPessoaSalva;
		 } catch (FeignException e) {
	            log.error("Erro ao chamar a API de Pessoas. Status: {}, Corpo: {}", e.status(), e.contentUTF8(), e);
	            if (e.status() == 400) {
	                throw new IllegalArgumentException("Dados inválidos enviados para a API de Pessoas: " + e.contentUTF8());
	            }
	            if (e.status() == 401 || e.status() == 403) {
	                 throw new IllegalStateException("Falha de autenticação/autorização com a API de Pessoas.");
	            }
	            throw new RuntimeException("Erro de comunicação com a API de Pessoas.", e);
	    }
	}
	
}
