package com.sw.tse.api.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
	
	@Operation(summary = "Gerar segunda via de boleto", 
	           description = "Gera e retorna o PDF da segunda via de boleto para uma conta financeira específica. " +
	                        "Apenas boletos não pagos podem ter segunda via gerada.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Segunda via de boleto gerada com sucesso",
					content = @Content(mediaType = "application/pdf")),
			@ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não é um boleto",
					content = @Content),
			@ApiResponse(responseCode = "404", description = "Conta financeira não encontrada",
					content = @Content),
			@ApiResponse(responseCode = "403", description = "Conta não pertence ao cliente autenticado",
					content = @Content),
			@ApiResponse(responseCode = "500", description = "Erro interno do servidor",
					content = @Content)
	})
	@PostMapping("/segundaviaboleto/{idContaFinanceira}")
	public ResponseEntity<byte[]> gerarSegundaViaBoleto(
			@Parameter(description = "ID da conta financeira para gerar segunda via do boleto", required = true)
			@PathVariable Long idContaFinanceira) {
		
		byte[] pdfBytes = contaFinanceiraService.gerarSegundaViaBoleto(idContaFinanceira);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "boleto-" + idContaFinanceira + ".pdf");
		headers.setContentLength(pdfBytes.length);
		
		return ResponseEntity.ok()
				.headers(headers)
				.body(pdfBytes);
	}
	
}
