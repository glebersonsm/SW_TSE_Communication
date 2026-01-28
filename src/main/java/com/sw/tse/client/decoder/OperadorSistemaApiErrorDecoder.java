package com.sw.tse.client.decoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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
        String responseBody = null;
        if (response.body() != null) {
            try {
                responseBody = IOUtils.toString(response.body().asInputStream(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.warn("Falha ao ler o corpo do erro da API: {}", e.getMessage());
            }
        }

        logResponseFull(methodKey, response, responseBody);

        if (responseBody == null || responseBody.isBlank()) {
            String fallback = "Erro da API (HTTP %d %s)".formatted(response.status(), response.reason());
            return new ApiTseException(fallback);
        }

        ApiTseErrorResponse errorResponse = null;
        try {
            errorResponse = objectMapper.readValue(responseBody, ApiTseErrorResponse.class);
        } catch (IOException e) {
            log.warn("Não foi possível desserializar o corpo do erro: {}", responseBody);
        }

        String errorMessage = (errorResponse != null && errorResponse.message() != null)
                ? errorResponse.message()
                : "Erro desconhecido da API.";

        return new ApiTseException(errorMessage);
    }

    /** TEMPORÁRIO: log completo do response (status, headers, body) em erros. Remover após debug. */
    private void logResponseFull(String methodKey, Response response, String responseBody) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("\n[TSE API Response] ").append(methodKey);
            sb.append("\n  Status: ").append(response.status()).append(" ").append(response.reason());
            if (response.headers() != null && !response.headers().isEmpty()) {
                String headers = response.headers().entrySet().stream()
                        .map(e -> e.getKey() + ": " + String.join(", ", e.getValue()))
                        .collect(Collectors.joining(" | "));
                sb.append("\n  Headers: ").append(headers);
            }
            sb.append("\n  Body: ").append(responseBody != null && !responseBody.isBlank() ? responseBody : "(vazio)");
            log.warn("{}", sb);
        } catch (Exception e) {
            log.warn("Falha ao montar log do response: {}", e.getMessage());
        }
    }
}