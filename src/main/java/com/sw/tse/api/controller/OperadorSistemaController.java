package com.sw.tse.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.OperadorSistemaRequestDto;
import com.sw.tse.domain.model.api.response.OperadorSistemaCriadoApiResponse;
import com.sw.tse.domain.model.api.response.OperadorSistemaListaApiResponse;
import com.sw.tse.domain.service.interfaces.OperadorSistemaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/operadorsistema")
@RequiredArgsConstructor
public class OperadorSistemaController {

    private final OperadorSistemaService operadorSistemaService;
    
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<OperadorSistemaListaApiResponse>>> listarTodos() {
        List<OperadorSistemaListaApiResponse> operadores = operadorSistemaService.listarTodos();
        
        ApiResponseDto<List<OperadorSistemaListaApiResponse>> responseApi = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            operadores,
            "Operadores de sistema listados com sucesso"
        );
        
        return ResponseEntity.ok(responseApi);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<OperadorSistemaListaApiResponse>> buscarPorId(@PathVariable("id") Long id) {
        OperadorSistemaListaApiResponse operador = operadorSistemaService.buscarPorId(id);
        
        ApiResponseDto<OperadorSistemaListaApiResponse> responseApi = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            operador,
            "Operador de sistema encontrado com sucesso"
        );
        
        return ResponseEntity.ok(responseApi);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponseDto<OperadorSistemaCriadoApiResponse>> criarOperadorSistema(@RequestBody OperadorSistemaRequestDto requestDto) {
    	OperadorSistemaCriadoApiResponse operadorSistemaCriado = operadorSistemaService.criarOperadorSistema(requestDto);
        
        ApiResponseDto<OperadorSistemaCriadoApiResponse> responseApi = new ApiResponseDto<>(
            HttpStatus.CREATED.value(),
            true,
            operadorSistemaCriado,
            "Operador de sistema criado com sucesso"
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseApi);
    }
}