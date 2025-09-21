package com.sw.tse.client.config;

import org.springframework.context.annotation.Bean;

import com.sw.tse.client.decoder.PessoaApiErrorDecoder;

import feign.codec.ErrorDecoder;


public class PessoaClientConfig {
	
    @Bean
    ErrorDecoder pessoaErrorDecoder() {
        return new PessoaApiErrorDecoder();
    }
    
}