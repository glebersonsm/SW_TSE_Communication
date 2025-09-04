package com.sw.tse.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "criptografiaApiClient", url = "${api.tse.url}")
public interface CriptografiaApiClient {


    @GetMapping("/api/cadastros/CriptografarDadosString/{texto}")
    String criptografarString(
            @PathVariable("texto") String texto,
            @RequestHeader("Authorization") String token
    );


    @GetMapping("/api/cadastros/CriptografarDadosString/{data}")
    String criptografarData(
            String data,
            @RequestHeader("Authorization") String token
    );


}