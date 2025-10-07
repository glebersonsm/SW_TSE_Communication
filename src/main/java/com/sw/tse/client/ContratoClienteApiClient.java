package com.sw.tse.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.client.config.GeralClientConfig;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;

@FeignClient(
    name = "contratoClienteApiClient",
    url = "${api.tse.url}",
    configuration = GeralClientConfig.class
)
public interface ContratoClienteApiClient {

    @GetMapping("/api/PainelDoCliente/MeusContratos")
    List<ContratoClienteApiResponse> buscarMeusContratos(
        @RequestHeader("Authorization") String tokenCliente);
}