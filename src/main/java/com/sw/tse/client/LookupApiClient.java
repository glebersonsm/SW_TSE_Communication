package com.sw.tse.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.controller.model.TipoEnderecoDto;

@FeignClient(name = "lookupApiClient", url = "${api.tse.url}")
public interface LookupApiClient {

	@PostMapping("/api/Lookup/TiposEndereco")
	public List<TipoEnderecoDto> listarTiposEndereco(
			@RequestHeader("Authorization") String token
	);


}


	
