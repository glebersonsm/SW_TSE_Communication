package com.sw.tse.api.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.HospedeDto;
import com.sw.tse.api.dto.PessoaComProprietarioResponse;
import com.sw.tse.domain.converter.PessoaConverter;
import com.sw.tse.domain.model.api.request.PessoaApiRequest;
import com.sw.tse.domain.service.interfaces.PessoaService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/pessoa")
@RestController
@RequiredArgsConstructor
@Tag(name = "Pessoa", description = "Endpoints para gestão de pessoas")
public class PessoaController {

	private final PessoaService pessoaService;
	private final PessoaConverter pessoaConverter;
	
	@Operation(summary = "Salvar pessoa", description = "Cria ou atualiza uma pessoa no sistema TSE")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Pessoa salva com sucesso",
					content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor",
					content = @Content)
	})
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
	
	@Operation(summary = "Buscar pessoa por CPF", description = "Busca pessoa por CPF com informações completas e verifica se é proprietário do contrato")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Pessoa encontrada",
					content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
			@ApiResponse(responseCode = "404", description = "Pessoa não encontrada",
					content = @Content),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor",
					content = @Content)
	})
	@GetMapping("/buscarPorCpf/{cpf}")
	public ResponseEntity<ApiResponseDto<PessoaComProprietarioResponse>> buscarPorCpf(
			@PathVariable String cpf,
			@RequestParam(required = false) Long idContrato) {
		
		Optional<PessoaComProprietarioResponse> pessoaOpt = pessoaService.buscarPorCpfCompleto(cpf, idContrato);
		
		if (pessoaOpt.isEmpty()) {
			ApiResponseDto<PessoaComProprietarioResponse> respostaApi = new ApiResponseDto<>(
				HttpStatus.NOT_FOUND.value(),
				false,
				null,
				"Pessoa não encontrada com o CPF informado."
			);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respostaApi);
		}
		
		ApiResponseDto<PessoaComProprietarioResponse> respostaApi = new ApiResponseDto<>(
			HttpStatus.OK.value(),
			true,
			pessoaOpt.get(),
			"Pessoa encontrada com sucesso."
		);
		
		return ResponseEntity.ok(respostaApi);
	}
}
