package com.sw.tse.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sw.tse.client.decoder.GeralApiErrorDecoder;

import feign.codec.ErrorDecoder;


@Configuration
public class GeralClientConfig {
	
    @Bean
    ErrorDecoder GeralApiErrorDecoder() {
        return new GeralApiErrorDecoder();
    }
    
}