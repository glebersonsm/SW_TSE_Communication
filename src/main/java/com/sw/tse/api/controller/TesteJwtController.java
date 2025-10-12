package com.sw.tse.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.security.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teste-jwt")
public class TesteJwtController {
    
    @GetMapping("/dados-cliente")
    public ResponseEntity<ApiResponseDto<Object>> obterDadosCliente() {
        
        // Extrai os dados do token JWT da requisição atual
        Long idUsuarioCliente = JwtTokenUtil.getIdUsuarioCliente();
        String tokenUsuarioCliente = JwtTokenUtil.getTokenUsuarioCliente();
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        String dados = String.format("ID Usuário Cliente: %s | Token Usuário Cliente: %s | ID Pessoa Cliente: %s", 
                                   idUsuarioCliente, tokenUsuarioCliente, idPessoaCliente);
        
        ApiResponseDto<Object> response = new ApiResponseDto<>(
            200,
            true,
            dados,
            "Dados extraídos do token JWT com sucesso"
        );
        
        return ResponseEntity.ok(response);
    }
}
