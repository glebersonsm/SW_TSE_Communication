package com.sw.tse.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/painelcliente")
@Tag(name = "Conta Financeira Cliente", description = "Endpoints para gestão de contas financeiras do cliente")
public class ContaFinanceiraClienteController {
	
	private final ContaFinanceiraService contaFinanceiraService;
	
	@Operation(summary = "Listar contas financeiras do cliente", 
	           description = "Lista todas as contas financeiras do cliente, excluindo contas com tipo histórico RENEGOCIADA, EXCLUIDO ou CANCELADO")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Contas financeiras listadas com sucesso",
					content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "Dados inválidos",
					content = @Content),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor",
					content = @Content)
	})
	@GetMapping("/contasfinanceiras")
	public ResponseEntity<ApiResponseDto<List<ContaFinanceiraClienteDto>>> listarContasFinanceiras() {
		
		List<ContaFinanceiraClienteDto> contasDto = contaFinanceiraService.buscarContasClienteDto();
		
		ApiResponseDto<List<ContaFinanceiraClienteDto>> responseApi = new ApiResponseDto<>(
				HttpStatus.OK.value(),
				true,
				contasDto,
				"Contas financeiras listadas com sucesso"
			);
		
		return ResponseEntity.ok(responseApi);
	}
	
}
