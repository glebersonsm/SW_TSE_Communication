package com.sw.tse.api.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ApiResponseDto;
import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.api.dto.PaginatedResponseDto;
import com.sw.tse.api.dto.ReservaResumoResponse;
import com.sw.tse.api.dto.ReservaSemanaResponse;
import com.sw.tse.api.dto.ReservarSemanaRequest;
import com.sw.tse.api.dto.SemanasDisponiveisRequest;
import com.sw.tse.api.dto.SemanasDisponiveisResponse;
import com.sw.tse.domain.model.dto.CancelarReservaRequest;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.model.dto.PeriodoUtilizacaoDisponivel;
import com.sw.tse.domain.service.interfaces.CancelarReservaService;
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
    private final CancelarReservaService cancelarReservaService;

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
               description = "Cria uma reserva completa para um período de utilização disponível")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva criada com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Contrato bloqueado ou inadimplente", content = @Content),
            @ApiResponse(responseCode = "409", description = "Período não disponível", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/reservar")
    public ResponseEntity<ApiResponseDto<ReservaSemanaResponse>> reservarSemana(
            @Valid @RequestBody ReservarSemanaRequest request) {

        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        // Verificar se é criação ou edição
        boolean isEdicao = request.getIdUtilizacaoContrato() != null && request.getIdUtilizacaoContrato() > 0;
        
        // Delega toda a lógica de negócio para o service
        ReservaSemanaResponse reserva = reservarSemanaService.criarReserva(request, idPessoaCliente);

        // Definir status e mensagem com base na operação
        HttpStatus status = isEdicao ? HttpStatus.OK : HttpStatus.CREATED;
        String mensagem = isEdicao ? "Reserva editada com sucesso" : "Reserva criada com sucesso";

        ApiResponseDto<ReservaSemanaResponse> responseApi = new ApiResponseDto<>(
                status.value(),
                true,
                reserva,
                mensagem
        );

        return ResponseEntity.status(status).body(responseApi);
    }
    
    @Operation(summary = "Buscar utilização de contrato", 
               description = "Busca os dados de uma utilização de contrato existente (RESERVA, RCI ou POOL)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilização encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Utilização não encontrada"),
            @ApiResponse(responseCode = "403", description = "Contrato não pertence ao cliente")
    })
    @GetMapping("/reservar/{idUtilizacaoContrato}")
    public ResponseEntity<ApiResponseDto<ReservaSemanaResponse>> buscarUtilizacao(
            @Parameter(description = "ID da utilização de contrato a ser buscada", required = true)
            @PathVariable Long idUtilizacaoContrato) {
        
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        ReservaSemanaResponse utilizacao = reservarSemanaService.buscarUtilizacao(idUtilizacaoContrato, idPessoaCliente);
        
        ApiResponseDto<ReservaSemanaResponse> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                utilizacao,
                "Utilização encontrada com sucesso"
        );
        
        return ResponseEntity.ok(responseApi);
    }
    
    @Operation(summary = "Listar reservas por ano", 
               description = "Lista todas as utilizações (RESERVA, RCI, POOL) não canceladas de um ano específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Cliente não autenticado")
    })
    @GetMapping("/reservas/{ano}")
    public ResponseEntity<ApiResponseDto<List<ReservaResumoResponse>>> listarReservasPorAno(
            @Parameter(description = "Ano das reservas (ex: 2024)", required = true)
            @PathVariable int ano) {
        
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        List<ReservaResumoResponse> reservas = reservarSemanaService.listarReservasPorAno(ano, idPessoaCliente);
        
        ApiResponseDto<List<ReservaResumoResponse>> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                reservas,
                String.format("Encontradas %d reservas para o ano %d", reservas.size(), ano)
        );
        
        return ResponseEntity.ok(responseApi);
    }
    
    @Operation(summary = "Listar reservas por contrato e ano", 
               description = "Lista todas as utilizações (RESERVA, RCI, POOL) não canceladas de um contrato específico em um ano")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Cliente não autenticado"),
            @ApiResponse(responseCode = "403", description = "Contrato não pertence ao cliente")
    })
    @GetMapping("/reservas/{idContrato}/{ano}")
    public ResponseEntity<ApiResponseDto<List<ReservaResumoResponse>>> listarReservasPorContratoEAno(
            @Parameter(description = "ID do contrato", required = true)
            @PathVariable Long idContrato,
            @Parameter(description = "Ano das reservas (ex: 2024)", required = true)
            @PathVariable int ano) {
        
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        List<ReservaResumoResponse> reservas = reservarSemanaService.listarReservasPorContratoEAno(idContrato, ano, idPessoaCliente);
        
        ApiResponseDto<List<ReservaResumoResponse>> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                reservas,
                String.format("Encontradas %d reservas para o contrato %d no ano %d", reservas.size(), idContrato, ano)
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
               description = "Lista todas as contas financeiras do cliente com filtros e paginação opcionais, excluindo contas com tipo histórico RENEGOCIADA, EXCLUIDO ou CANCELADO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contas financeiras listadas com sucesso",
                    content = @Content(schema = @Schema(implementation = PaginatedResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/contasfinanceiras")
    public ResponseEntity<PaginatedResponseDto<ContaFinanceiraClienteDto>> listarContasFinanceiras(
            @Parameter(description = "Data inicial de vencimento (formato: yyyy-MM-dd)", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoInicial,
            
            @Parameter(description = "Data final de vencimento (formato: yyyy-MM-dd)", required = false)
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoFinal,
            
            @Parameter(description = "Status da conta: B (Paga), P (Em aberto), V (Vencida)", required = false)
            @RequestParam(required = false) String status,
            
            @Parameter(description = "Número da página (inicia em 1)", required = false)
            @RequestParam(required = false, defaultValue = "1") Integer numeroDaPagina,
            
            @Parameter(description = "Quantidade de registros por página", required = false)
            @RequestParam(required = false, defaultValue = "30") Integer quantidadeRegistrosRetornar) {
        
        var resultado = contaFinanceiraService.buscarContasClienteDtoComPaginacao(
                vencimentoInicial, 
                vencimentoFinal, 
                status,
                numeroDaPagina,
                quantidadeRegistrosRetornar
        );
        
        PaginatedResponseDto<ContaFinanceiraClienteDto> responseApi = new PaginatedResponseDto<>(
                HttpStatus.OK.value(),
                true,
                resultado.getContas(),
                "Contas financeiras listadas com sucesso",
                resultado.getPageNumber(),
                resultado.getLastPageNumber()
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
    
    // ==================== ENDPOINTS DE CANCELAMENTO ====================
    
    @Operation(summary = "Cancelar reserva", 
               description = "Cancela uma utilizacao de contrato (RESERVA ou RCI) e seu periodo modelo cota")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Utilizacao nao encontrada"),
            @ApiResponse(responseCode = "400", description = "Cancelamento nao permitido ou fora do prazo")
    })
    @DeleteMapping("/reservar")
    public ResponseEntity<ApiResponseDto<Void>> cancelarReserva(
            @Valid @RequestBody CancelarReservaRequest request) {
        
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente nao esta disponivel no token");
        }
        
        cancelarReservaService.cancelarReserva(
            request.getIdUtilizacaoContrato(), 
            request.getMotivo(), 
            idPessoaCliente
        );
        
        ApiResponseDto<Void> responseApi = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            null,
            "Reserva cancelada com sucesso"
        );
        
        return ResponseEntity.ok(responseApi);
    }
}
