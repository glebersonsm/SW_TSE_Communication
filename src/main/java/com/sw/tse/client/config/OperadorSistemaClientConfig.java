package com.sw.tse.client.config;

import org.springframework.context.annotation.Bean;

import com.sw.tse.client.decoder.OperadorSistemaApiErrorDecoder;

import feign.Logger;
import feign.codec.ErrorDecoder;


public class OperadorSistemaClientConfig {

    /** TEMPORÁRIO: log FULL (request + response) para debug. Remover após entender erros 401/400. */
    @Bean
    Logger.Level operadorSistemaFeignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    ErrorDecoder operadorSistemaErrorDecoder() {
        return new OperadorSistemaApiErrorDecoder();
    }
}