package com.sw.tse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.client.config.PessoaClientConfig;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.model.api.response.PessoaCpfApiResponse;

@FeignClient(
		name = "pessoaApiClient",
		url = "${api.tse.url}",
		configuration = PessoaClientConfig.class
)
public interface PessoaApiClient {

	@PostMapping("/api/cadastros/SetPessoa/{id}")
	public String salvarPessoa(
			@PathVariable Long id,
			@RequestHeader("Authorization") String token,
			@RequestBody PessoaApiRequest pessoaRequest
	);

	@GetMapping("/api/cadastros/GetPessoaCpf/{cpf}")
	public PessoaCpfApiResponse buscarPorCpf(
			@PathVariable String cpf,
			@RequestHeader("Authorization") String token
	);

	@GetMapping("/api/cadastros/GetPessoa/{id}")
	public PessoaApiRequest buscarPorId(
			@PathVariable Long id,
			@RequestHeader("Authorization") String token
	);

}


	
