package com.sw.tse.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.domain.model.api.response.TipoDocumentoPessoaApiResponse;
import com.sw.tse.domain.service.interfaces.TipoDocumentoPessoaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/lookup")
@RequiredArgsConstructor
@Tag(name = "Lookup", description = "Endpoints para consulta de dados de referência")
public class LookupController {

	private final TipoDocumentoPessoaService tipoDocumentoPessoaService;
	

	@Operation(summary = "Listar tipos de documento", description = "Retorna todos os tipos de documento de pessoa disponíveis")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Tipos de documento listados com sucesso",
					content = @Content(schema = @Schema(implementation = TipoDocumentoPessoaApiResponse.class))),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor",
					content = @Content)
	})
	@GetMapping("TiposDocumentoPessoa")
	public List<TipoDocumentoPessoaApiResponse> listarTiposDocumento(){
		return tipoDocumentoPessoaService.listarTiposDocumento();
	}
	
}
