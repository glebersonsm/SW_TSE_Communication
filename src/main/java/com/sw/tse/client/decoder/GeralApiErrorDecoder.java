package com.sw.tse.client.decoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.response.ApiTseErrorResponse;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeralApiErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String responseBody;
        try {
            responseBody = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new RuntimeException("Falha ao ler o corpo do erro da API.", e);
        }

        // Tratamento específico para timeout
        if (response.status() == 504) {
            log.warn("Timeout na chamada da API externa: {}", methodKey);
            return new ApiTseException("Serviço externo não respondeu a tempo. Tente novamente em alguns instantes.");
        }

        ApiTseErrorResponse errorResponse = null;
        try {
            errorResponse = objectMapper.readValue(responseBody, ApiTseErrorResponse.class);
        } catch (IOException e) {
            log.warn("Não foi possível desserializar o corpo do erro (status {}): {}", response.status(), responseBody);
            // Retornar o corpo bruto quando não conseguir deserializar
            String errorMessage = "Erro na API externa (HTTP " + response.status() + "): " + responseBody;
            return new ApiTseException(errorMessage);
        }
        
        // Se deserializou mas não tem mensagem, incluir o corpo bruto também
        String errorMessage;
        if (errorResponse != null && errorResponse.message() != null && !errorResponse.message().trim().isEmpty()) {
            errorMessage = errorResponse.message();
        } else {
            errorMessage = "Erro na API externa (HTTP " + response.status() + "): " + responseBody;
        }
        

        return new ApiTseException(errorMessage);
    }
}