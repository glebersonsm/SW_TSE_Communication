package com.sw.tse.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.domain.model.api.response.OperadorSistemaDto;
import com.sw.tse.domain.model.api.response.TipoEnderecoDto;
import com.sw.tse.domain.model.api.response.TipoLogradouroDto;

@FeignClient(name = "lookupApiClient", url = "${api.tse.url}")
public interface LookupApiClient {

	@GetMapping("/api/Lookup/TiposEndereco")
	public List<TipoEnderecoDto> listarTiposEndereco(
			@RequestHeader("Authorization") String token
	);

	@GetMapping("/api/Lookup/TiposLogradouro")
	public List<TipoLogradouroDto> listarTiposLogradouro(
			@RequestHeader("Authorization") String token
	);
	
	@GetMapping("/api/Lookup/OperadoresSistema")
	public List<OperadorSistemaDto> listarOperadoresSistema(
			@RequestHeader("Authorization") String token
	);

}


	
