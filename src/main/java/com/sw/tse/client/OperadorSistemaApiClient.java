package com.sw.tse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.client.config.OperadorSistemaApiClientConfig;
import com.sw.tse.domain.model.api.request.OperadorSistemaApiRequest;
import com.sw.tse.domain.model.api.response.OperadorSistemaApiResponse;

@FeignClient(
	    name = "operadorSistemaApiClient", 
	    url = "${api.tse.url}", 
	    configuration = OperadorSistemaApiClientConfig.class
	)
public interface OperadorSistemaApiClient {
    @PostMapping("/api/OperadorSistema/SetEmpresaSel/{idEmpresa}")
    void setEmpresaSessao(@PathVariable Long idEmpresa,
                          @RequestHeader("Authorization") String token);
    
    @PostMapping("/api/operadorsistema/SetOperadorSistema")
    OperadorSistemaApiResponse criarOperadorSistema(
        @RequestHeader("Authorization") String token,
        @RequestBody OperadorSistemaApiRequest request
    );
}
