package com.sw.tse.client.decoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.response.ApiTseErrorResponse;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PessoaApiErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody;
        try {
            responseBody = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new RuntimeException("Falha ao ler o corpo do erro da API.", e);
        }

        ApiTseErrorResponse errorResponse = null;
        try {
            errorResponse = objectMapper.readValue(responseBody, ApiTseErrorResponse.class);
        } catch (IOException e) {
            log.warn("Não foi possível desserializar o corpo do erro: {}", responseBody);
        }
        
        String errorMessage = (errorResponse != null) ? errorResponse.message() : "Erro desconhecido da API.";
        
        if(StringUtils.hasText(errorMessage) && "Atenção: Não foi encontrada nenhuma pessoa com este CPF.".equals(errorMessage)) {
        	errorMessage = "Não localizado em nossa base de clientes, um cliente com cpf informado";
        }

        return new ApiTseException(errorMessage);
    }
}