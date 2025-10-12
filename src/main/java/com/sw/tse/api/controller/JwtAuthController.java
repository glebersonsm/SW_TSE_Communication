package com.sw.tse.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.JwtRequestDto;
import com.sw.tse.api.dto.JwtResponseDto;
import com.sw.tse.security.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class JwtAuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    @PostMapping("/generate-token")
    public ResponseEntity<ApiResponseDto<JwtResponseDto>> generateToken(@RequestBody JwtRequestDto request) {
        
        // Autentica as credenciais
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(), 
                request.password()
            )
        );
        
        // Carrega o usuário
        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        
        // Gera token JWT com claims customizados
        String token = jwtService.generateToken(user, "1", "portal-fake");
        
        // Cria resposta
        JwtResponseDto response = new JwtResponseDto(
            token,
            "Bearer",
            2478L,
            "token-fake-cliente-2478",
            37418L
        );
        
        ApiResponseDto<JwtResponseDto> apiResponse = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            response,
            "Token JWT gerado com sucesso"
        );
        
        return ResponseEntity.ok(apiResponse);
    }
}
