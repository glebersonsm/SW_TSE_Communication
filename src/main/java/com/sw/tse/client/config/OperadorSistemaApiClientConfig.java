package com.sw.tse.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sw.tse.client.decoder.OperadorSistemaApiErrorDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class OperadorSistemaApiClientConfig {
    @Bean
    ErrorDecoder operadorSistemaErrorDecoder() {
        return new OperadorSistemaApiErrorDecoder();
    }
}