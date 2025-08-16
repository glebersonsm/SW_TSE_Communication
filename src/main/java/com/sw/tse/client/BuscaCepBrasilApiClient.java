package com.sw.tse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sw.tse.domain.model.api.response.BuscaCepBrasilApiResponse;

@FeignClient(name = "brasilApiBuscaCep", url = "https://brasilapi.com.br")
public interface BuscaCepBrasilApiClient {

	@GetMapping("/api/cep/v1/{cep}")
	public BuscaCepBrasilApiResponse buscarPorCep(
			@PathVariable String cep
	);

}


	
