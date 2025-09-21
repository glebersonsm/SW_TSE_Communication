package com.sw.tse.client.config;

import org.springframework.context.annotation.Bean;

import com.sw.tse.client.decoder.OperadorSistemaApiErrorDecoder;

import feign.codec.ErrorDecoder;


public class OperadorSistemaClientConfig {
	
    @Bean
    ErrorDecoder operadorSistemaErrorDecoder() {
        return new OperadorSistemaApiErrorDecoder();
    }
    
}