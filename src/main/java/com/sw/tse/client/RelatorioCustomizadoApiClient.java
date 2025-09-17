package com.sw.tse.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.domain.model.api.request.FiltroRelatorioCustomizadoApiRequest;

import feign.Response;

@FeignClient(name = "relatorioCustomizado", url = "${api.tse.url}")
public interface RelatorioCustomizadoApiClient {

	@PostMapping("/api/Relatorios/ObterJsonDadosRelatorioComParametros/{id}")
	Response obterDadosRelatorioRaw(
			@PathVariable Long id,
			@RequestHeader("Authorization") String token,
			@RequestBody List<FiltroRelatorioCustomizadoApiRequest> filtros
	);

}


	
