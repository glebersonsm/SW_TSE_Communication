package com.sw.tse.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sw.tse.api.dto.ApiResponseDto;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException, StreamWriteException, DatabindException, java.io.IOException {

        String mensagemAmigavel;		 	
        
        if (authException instanceof BadCredentialsException) {
            mensagemAmigavel = authException.getMessage();
        } else {
            mensagemAmigavel = "Token de autenticação não fornecido ou inválido. Por favor, inclua um token Bearer válido no cabeçalho Authorization.";
        }		 	
        
        ApiResponseDto<?> errorResponse = new ApiResponseDto<>(
            HttpStatus.UNAUTHORIZED.value(),
            false,
            null,
            String.format("Acesso negado: %s", mensagemAmigavel)
        );
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
