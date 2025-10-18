package com.sw.tse.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.api.dto.ReservarSemanaRequest;
import com.sw.tse.api.dto.SemanasDisponiveisRequest;
import com.sw.tse.api.dto.SemanasDisponiveisResponse;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.model.dto.PeriodoUtilizacaoDisponivel;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;
import com.sw.tse.domain.service.interfaces.PeriodoUtilizacaoService;
import com.sw.tse.domain.service.interfaces.ReservarSemanaService;
import com.sw.tse.security.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/painelcliente")
@RequiredArgsConstructor
@Tag(name = "Painel Cliente", description = "Endpoints para o painel do cliente")
public class PainelClienteController {

    private final PeriodoUtilizacaoService periodoUtilizacaoService;
    private final ContaFinanceiraService contaFinanceiraService;
    private final ContratoClienteService contratoClienteService;
    private final ReservarSemanaService reservarSemanaService;

    // ==================== ENDPOINTS DE SEMANAS DISPONÍVEIS ====================
    
    @Operation(summary = "Buscar semanas disponíveis", 
               description = "Busca os períodos de utilização disponíveis para reserva de um contrato específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Semanas disponíveis listadas com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/semanas-disponivel")
    public ResponseEntity<ApiResponseDto<List<SemanasDisponiveisResponse>>> buscarSemanasDisponiveis(
            @Valid @RequestBody SemanasDisponiveisRequest request) {

        List<PeriodoUtilizacaoDisponivel> periodosDisponiveis = periodoUtilizacaoService
                .buscarPeriodosDisponiveisParaReserva(request.getIdcontrato(), request.getAno());

        List<SemanasDisponiveisResponse> response = periodosDisponiveis.stream()
                .map(this::mapearParaResponse)
                .collect(Collectors.toList());

        ApiResponseDto<List<SemanasDisponiveisResponse>> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                response,
                "Semanas disponíveis listadas com sucesso"
        );

        return ResponseEntity.ok(responseApi);
    }

    private SemanasDisponiveisResponse mapearParaResponse(PeriodoUtilizacaoDisponivel dto) {
        return SemanasDisponiveisResponse.builder()
                .idPeriodoUtilizacao(dto.getIdPeriodoUtilizacao())
                .descricaoPeriodo(dto.getDescricaoPeriodo())
                .checkin(dto.getCheckin())
                .checkout(dto.getCheckout())
                .idTipoPeriodoUtilizacao(dto.getIdTipoPeriodoUtilizacao())
                .descricaoTipoPeriodo(dto.getDescricaoTipoPeriodo())
                .ano(dto.getAno())
                .reserva(dto.getReserva())
                .rci(dto.getRci())
                .pool(dto.getPool())
                .build();
    }
    
    @Operation(summary = "Reservar semana disponível", 
               description = "Valida e cria uma reserva para um período de utilização disponível")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Período validado com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Contrato bloqueado ou inadimplente", content = @Content),
            @ApiResponse(responseCode = "409", description = "Período não disponível", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/reservar")
    public ResponseEntity<ApiResponseDto<String>> reservarSemana(
            @Valid @RequestBody ReservarSemanaRequest request) {

        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        // Delega toda a lógica de negócio para o service
        reservarSemanaService.validarReserva(
            request.getIdContrato(), 
            request.getIdPeriodoUtilizacao(),
            idPessoaCliente
        );

        ApiResponseDto<String> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                "VALIDADO",
                "Período validado com sucesso e disponível para reserva"
        );

        return ResponseEntity.ok(responseApi);
    }

    // ==================== ENDPOINTS DE CONTRATOS ====================
    
    @Operation(summary = "Buscar meus contratos", 
               description = "Lista todos os contratos do cliente autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contratos listados com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/meuscontratos")
    public ResponseEntity<ApiResponseDto<List<ContratoClienteApiResponse>>> buscarMeusContratos() {
        
        List<ContratoClienteApiResponse> listaContratos = contratoClienteService.buscarContratosCliente();
        
        ApiResponseDto<List<ContratoClienteApiResponse>> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                listaContratos,
                "Contratos listados com sucesso"
        );
        
        return ResponseEntity.ok(responseApi);
    }

    // ==================== ENDPOINTS DE CONTAS FINANCEIRAS ====================
    
    @Operation(summary = "Listar contas financeiras do cliente", 
               description = "Lista todas as contas financeiras do cliente, excluindo contas com tipo histórico RENEGOCIADA, EXCLUIDO ou CANCELADO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas financeiras listadas com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
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
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conta não é um boleto", content = @Content),
            @ApiResponse(responseCode = "404", description = "Conta financeira não encontrada", content = @Content),
            @ApiResponse(responseCode = "403", description = "Conta não pertence ao cliente autenticado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
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
