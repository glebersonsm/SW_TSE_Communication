package com.sw.tse.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/painelcliente/meuscontratos")
public class ContratoClienteController {
	
	private final ContratoClienteService contratoClienteService;
	
	@GetMapping
	public ResponseEntity<ApiResponseDto<List<ContratoClienteApiResponse>>> buscarMeusContratos(){
		
		List<ContratoClienteApiResponse> listaContratos = contratoClienteService.buscarContratosCliente();
		
		ApiResponseDto<List<ContratoClienteApiResponse>> responseApi = new ApiResponseDto<>(
			    HttpStatus.OK.value(),  
			    true,                       
			    listaContratos,  
			    "Contratos listados com sucesso"
			);
		
		return ResponseEntity.ok(responseApi);
	}
}
