package com.sw.tse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sw.tse.client.config.GeralClientConfig;

import feign.Response;

@FeignClient(
    name = "segundaViaBoletoApiClient",
    url = "${api.tse.url}",
    configuration = GeralClientConfig.class
)
public interface SegundaViaBoletoApiClient {

    @GetMapping("/api/PainelDoCliente/SegundaViaBoleto/{idContaFinanceira}")
    Response gerarSegundaViaBoleto(
        @PathVariable Long idContaFinanceira,
        @RequestHeader("Authorization") String token
    );
}
