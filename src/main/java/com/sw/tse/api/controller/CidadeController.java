package com.sw.tse.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.EnderecoCompletoCepResponse;
import com.sw.tse.domain.service.impl.db.CidadeDbServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cidade")
@RequiredArgsConstructor
public class CidadeController {

	private final CidadeDbServiceImpl cidadeDbService;
	
	@GetMapping("/{cep}")
	public ResponseEntity<ApiResponseDto<EnderecoCompletoCepResponse>> buscarPorCep(@PathVariable String cep) {
		EnderecoCompletoCepResponse endereco = cidadeDbService.buscarEnderecoCompletoPorCep(cep);
	
		ApiResponseDto<EnderecoCompletoCepResponse> responseApi = new ApiResponseDto<>(
		    HttpStatus.OK.value(),  
		    true,                       
		    endereco,  
		    "Endereço localizado com sucesso"
		);
		
		return ResponseEntity.ok(responseApi);
	}
}
