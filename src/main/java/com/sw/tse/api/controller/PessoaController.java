package com.sw.tse.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.service.interfaces.PessoaService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/pessoa")
@RestController
@RequiredArgsConstructor
public class PessoaController {

	private final PessoaService pessoaService;
	private final PessoaConverter pessoaConverter;
	
	@PostMapping
	public ResponseEntity<ApiResponseDto<Long>> salvar(@RequestBody HospedeDto request) {
		Long idPessoaSalva = pessoaService.salvar(request);
		ApiResponseDto<Long> respostaApi = new ApiResponseDto<>(
            HttpStatus.OK.value(),      
            true,                       
            idPessoaSalva,              
            "Pessoa salva com sucesso." 
        );
		return ResponseEntity.ok(respostaApi);
	}
	
	
	
	@PostMapping("/json")
	public PessoaApiRequest jsonPessoa(@RequestBody HospedeDto hospedeDto) {
		PessoaApiRequest request = pessoaConverter.toPessoaApiHospedeDto(hospedeDto);
		return request;
	}
}
