package com.sw.tse.client.decoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.domain.expection.ApiTseException;
import com.sw.tse.domain.model.api.response.ApiTseErrorResponse;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperadorSistemaApiErrorDecoder implements ErrorDecoder {
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

        if (errorMessage.contains("operador vinculado á pessoa")) {
            return new ApiTseException(errorMessage);
        }
        if (errorMessage.contains("Não encontrado pessoa com o id")) {
            return new ApiTseException(errorMessage);
        }
        if (errorMessage.contains("login inválido")) {
            return new ApiTseException(errorMessage);
        }
        if (response.status() == 401 || response.status() == 403) {
             return new AccessDeniedException(errorMessage); // Do Spring Security
        }

        return new ErrorDecoder.Default().decode(methodKey, response); // Erro genérico
    }
}