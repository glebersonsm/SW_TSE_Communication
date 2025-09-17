package com.sw.tse.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.domain.model.api.response.CidadeApiResponse;
import com.sw.tse.domain.service.interfaces.CidadeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cidade")
@RequiredArgsConstructor
public class CidadeController {

	private final CidadeService cidadeService;
	
	@GetMapping("/{cep}")
	public ResponseEntity<ApiResponseDto<CidadeApiResponse>> buscarPorCep(@PathVariable String cep) {
		CidadeApiResponse cidadeDto = cidadeService.buscarPorCep(cep);
	
		ApiResponseDto<CidadeApiResponse> responseApi = new ApiResponseDto<>(
		    HttpStatus.OK.value(),  
		    true,                       
		    cidadeDto,  
		    "Cidade localizada com sucesso"
		);
		
		return ResponseEntity.ok(responseApi);
	}
}
