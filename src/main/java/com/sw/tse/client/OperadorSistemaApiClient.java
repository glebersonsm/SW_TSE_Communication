package com.sw.tse.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.client.config.OperadorSistemaClientConfig;
import com.sw.tse.domain.model.api.request.OperadorSistemaApiRequest;
import com.sw.tse.domain.model.api.response.OperadorSistemaCriadoApiResponse;

@FeignClient(
	    name = "operadorSistemaApiClient", 
	    url = "${api.tse.url}", 
	    configuration = OperadorSistemaClientConfig.class
	)
public interface OperadorSistemaApiClient {
    @PostMapping("/api/OperadorSistema/SetEmpresaSel/{idEmpresa}")
    void setEmpresaSessao(@PathVariable Long idEmpresa,
                          @RequestHeader("Authorization") String token,
                          @RequestBody() Map<String, Object> corpoVazio);
    
    @PostMapping("/api/operadorsistema/SetOperadorSistema")
    OperadorSistemaCriadoApiResponse criarOperadorSistema(
        @RequestHeader("Authorization") String token,
        @RequestBody OperadorSistemaApiRequest request
    );
}
