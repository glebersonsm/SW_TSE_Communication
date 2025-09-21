package com.sw.tse.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.domain.model.api.response.OperadorSistemaListaApiResponse;
import com.sw.tse.domain.model.api.response.TipoEnderecoApiResponse;
import com.sw.tse.domain.model.api.response.TipoLogradouroApiResponse;

@FeignClient(name = "lookupApiClient", url = "${api.tse.url}")
public interface LookupApiClient {

	@GetMapping("/api/Lookup/TiposEndereco")
	public List<TipoEnderecoApiResponse> listarTiposEndereco(
			@RequestHeader("Authorization") String token
	);

	@GetMapping("/api/Lookup/TiposLogradouro")
	public List<TipoLogradouroApiResponse> listarTiposLogradouro(
			@RequestHeader("Authorization") String token
	);
	
	@GetMapping("/api/Lookup/OperadoresSistema")
	public List<OperadorSistemaListaApiResponse> listarOperadoresSistema(
			@RequestHeader("Authorization") String token
	);

}


	
