package com.sw.tse.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.service.impl.PessoaApiService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/pessoa")
@RestController
@RequiredArgsConstructor
public class PessoaController {

	private final PessoaApiService pessoaApiService;
	
	@PostMapping
	public String salvar(@RequestBody PessoaApiRequest request) {
		Long idPessoa = request.idPessoa() == null ? 0 : request.idPessoa();
		return pessoaApiService.salvarPessoa(idPessoa, request);
	}
}
