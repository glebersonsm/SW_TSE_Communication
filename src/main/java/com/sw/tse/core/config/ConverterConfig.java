package com.sw.tse.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sw.tse.core.util.GenericCryptoLocalDateConverter;
import com.sw.tse.core.util.GenericCryptoStringConverter;

@Configuration
public class ConverterConfig {

    @Bean
    public GenericCryptoStringConverter genericCryptoStringConverter() {
        return new GenericCryptoStringConverter();
    }

    @Bean
    public GenericCryptoLocalDateConverter genericCryptoLocalDateConverter() {
        return new GenericCryptoLocalDateConverter();
    }
}