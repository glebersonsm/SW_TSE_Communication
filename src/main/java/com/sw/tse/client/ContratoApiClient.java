package com.sw.tse.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.sw.tse.client.config.GeralClientConfig;
import com.sw.tse.domain.model.api.response.ContratoPessoaApiResponse;

@FeignClient(
		name = "contratoApiClient",
		url = "${api.tse.url}",
		configuration = GeralClientConfig.class
)
public interface ContratoApiClient {

	@GetMapping("/api/utilizacaocontrato/obtercontratos")
	public List<ContratoPessoaApiResponse> buscarContratoPorCpf(
			@RequestParam String cpf,
			@RequestHeader("Authorization") String token);
}
