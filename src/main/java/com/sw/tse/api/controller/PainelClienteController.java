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
import com.sw.tse.api.dto.BandeiraAceitaDto;
import com.sw.tse.api.dto.CartaoParaPagamentoDto;
import com.sw.tse.api.dto.CartaoVinculadoResponseDto;
import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.api.dto.DadosPessoaDto;
import com.sw.tse.api.dto.EnderecoDto;
import com.sw.tse.api.dto.PaginatedResponseDto;
import com.sw.tse.api.dto.ReservaResumoResponse;
import com.sw.tse.api.dto.ReservaSemanaResponse;
import com.sw.tse.api.dto.ReservarSemanaRequest;
import com.sw.tse.api.dto.SalvarCartaoRequestDto;
import com.sw.tse.api.dto.SemanasDisponiveisRequest;
import com.sw.tse.api.dto.SemanasDisponiveisResponse;
import com.sw.tse.api.dto.VoucherReservaResponse;
import com.sw.tse.domain.expection.TokenJwtInvalidoException;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.model.dto.CancelarReservaRequest;
import com.sw.tse.domain.model.dto.PeriodoUtilizacaoDisponivel;
import com.sw.tse.domain.service.interfaces.CancelarReservaService;
import com.sw.tse.domain.service.interfaces.ContaFinanceiraService;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;
import com.sw.tse.domain.service.interfaces.PeriodoUtilizacaoService;
import com.sw.tse.domain.service.interfaces.ReservarSemanaService;
import com.sw.tse.domain.service.interfaces.VoucherReservaService;
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
    private final VoucherReservaService voucherReservaService;
    private final CancelarReservaService cancelarReservaService;
    private final com.sw.tse.domain.repository.ContratoRepository contratoRepository;
    private final com.sw.tse.domain.service.interfaces.CartaoVinculadoPessoaService cartaoVinculadoPessoaService;
    private final com.sw.tse.domain.service.interfaces.PessoaService pessoaService;

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
                .buscarPeriodosDisponiveisParaReserva(request.getIdcontrato(), request.getAno(),
                        request.getTipoValidacaoIntegralizacao(), request.getValorIntegralizacao());

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
                .capacidade(dto.getCapacidade())
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
    
    @Operation(summary = "Obter dados para voucher de reserva",
               description = "Retorna os dados consolidados da reserva para geração de voucher em PDF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso"),
            @ApiResponse(responseCode = "404", description = "Reserva não encontrada"),
            @ApiResponse(responseCode = "403", description = "Reserva não pertence ao cliente autenticado")
    })
    @GetMapping("/reservas/{idUtilizacaoContrato}/voucher")
    public ResponseEntity<ApiResponseDto<VoucherReservaResponse>> obterDadosVoucherReserva(
            @Parameter(description = "ID da utilização de contrato", required = true)
            @PathVariable Long idUtilizacaoContrato) {
        
        Long idPessoaCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idPessoaCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token de autenticação");
        }
        
        var voucherOptional = voucherReservaService.obterDadosVoucherReserva(idUtilizacaoContrato, idPessoaCliente);
        if (voucherOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponseDto<>(
                            HttpStatus.NOT_FOUND.value(),
                            false,
                            null,
                            "Contrato associado à reserva não encontrado"));
        }

        ApiResponseDto<VoucherReservaResponse> responseApi = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                voucherOptional.get(),
                "Dados da reserva obtidos com sucesso"
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
            
            @Parameter(description = "ID da empresa para filtrar contas", required = false)
            @RequestParam(required = false) Long empresaId,
            
            @Parameter(description = "Número da página (inicia em 1)", required = false)
            @RequestParam(required = false, defaultValue = "1") Integer numeroDaPagina,
            
            @Parameter(description = "Quantidade de registros por página", required = false)
            @RequestParam(required = false, defaultValue = "30") Integer quantidadeRegistrosRetornar) {
        
        var resultado = contaFinanceiraService.buscarContasClienteDtoComPaginacao(
                vencimentoInicial, 
                vencimentoFinal, 
                status,
                empresaId,
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

    // ==================== ENDPOINTS DE EMPRESAS DO USUÁRIO ====================
    
    @Operation(summary = "Listar empresas do usuário logado", 
               description = "Lista as empresas onde o usuário tem contratos com status ATIVO ou ATIVOREV")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresas listadas com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/empresas-usuario-logado")
    public ResponseEntity<ApiResponseDto<List<com.sw.tse.api.model.EmpresaTseDto>>> listarEmpresasDoUsuario() {
        
        Long idCliente = JwtTokenUtil.getIdPessoaCliente();
        
        if (idCliente == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token");
        }
        
        List<com.sw.tse.api.model.EmpresaTseDto> empresas = contratoRepository.findEmpresasByPessoaComContratosAtivos(idCliente);
        
        ApiResponseDto<List<com.sw.tse.api.model.EmpresaTseDto>> responseApi = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            empresas,
            "Empresas listadas com sucesso"
        );
        
        return ResponseEntity.ok(responseApi);
    }
    
    // ==================== ENDPOINTS DE CARTÕES SALVOS ====================
    
    @Operation(summary = "Salvar cartão de crédito", 
               description = "Salva um cartão de crédito criptografado para o cliente logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão salvo com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @PostMapping("/cartao/salvar")
    public ResponseEntity<ApiResponseDto<CartaoVinculadoResponseDto>> salvarCartao(
            @Valid @RequestBody SalvarCartaoRequestDto dto) {
        
        Long pessoaId = JwtTokenUtil.getIdPessoaCliente();
        
        if (pessoaId == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token");
        }
        
        CartaoVinculadoResponseDto resultado = cartaoVinculadoPessoaService.salvarCartao(dto, pessoaId);
        
        ApiResponseDto<CartaoVinculadoResponseDto> response = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            resultado,
            "Cartão salvo com sucesso"
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Listar cartões salvos", 
               description = "Lista os cartões de crédito salvos do cliente logado (dados ofuscados)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartões listados com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/cartao/meus-cartoes")
    public ResponseEntity<ApiResponseDto<List<CartaoVinculadoResponseDto>>> listarMeusCartoes() {
        
        Long pessoaId = JwtTokenUtil.getIdPessoaCliente();
        
        if (pessoaId == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token");
        }
        
        List<CartaoVinculadoResponseDto> cartoes = cartaoVinculadoPessoaService.listarCartoesPessoa(pessoaId);
        
        ApiResponseDto<List<CartaoVinculadoResponseDto>> response = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            cartoes,
            "Cartões listados com sucesso"
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Obter dados do cartão para pagamento (uso interno)", 
               description = "Retorna os dados descriptografados do cartão para processar pagamento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados obtidos com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @GetMapping("/cartao/{id}/dados-pagamento")
    public ResponseEntity<ApiResponseDto<CartaoParaPagamentoDto>> obterDadosParaPagamento(
            @PathVariable Long id) {
        
        Long pessoaId = JwtTokenUtil.getIdPessoaCliente();
        
        if (pessoaId == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token");
        }
        
        CartaoParaPagamentoDto dados = cartaoVinculadoPessoaService.obterCartaoParaPagamento(id, pessoaId);
        
        ApiResponseDto<CartaoParaPagamentoDto> response = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            dados,
            "Dados do cartão obtidos com sucesso"
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Remover cartão salvo", 
               description = "Remove (soft delete) um cartão de crédito salvo do cliente logado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cartão removido com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cartão não encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content)
    })
    @DeleteMapping("/cartao/{id}")
    public ResponseEntity<ApiResponseDto<Void>> removerCartao(@PathVariable Long id) {
        
        Long pessoaId = JwtTokenUtil.getIdPessoaCliente();
        
        if (pessoaId == null) {
            throw new TokenJwtInvalidoException("ID da pessoa cliente não está disponível no token");
        }
        
        cartaoVinculadoPessoaService.removerCartao(id, pessoaId);
        
        ApiResponseDto<Void> response = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            null,
            "Cartão removido com sucesso"
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Listar bandeiras de cartão aceitas", 
               description = "Lista todas as bandeiras de cartão de crédito aceitas pelo sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bandeiras listadas com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    })
    @GetMapping("/cartao/bandeiras-aceitas")
    public ResponseEntity<ApiResponseDto<List<BandeiraAceitaDto>>> listarBandeirasAceitas() {
        List<BandeiraAceitaDto> bandeiras = cartaoVinculadoPessoaService.listarBandeirasAceitas();
        
        ApiResponseDto<List<BandeiraAceitaDto>> response = new ApiResponseDto<>(
            HttpStatus.OK.value(),
            true,
            bandeiras,
            "Bandeiras listadas com sucesso"
        );
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Obter dados da pessoa logada",
               description = "Retorna os dados cadastrais da pessoa logada, incluindo endereço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Pessoa não encontrada", content = @Content)
    })
    @GetMapping("/meus-dados")
    public ResponseEntity<ApiResponseDto<DadosPessoaDto>> obterMeusDados() {
        try {
            Long pessoaId = JwtTokenUtil.getIdPessoaCliente();
            DadosPessoaDto dadosPessoa = pessoaService.obterDadosPessoaLogada(pessoaId);
            
            ApiResponseDto<DadosPessoaDto> response = new ApiResponseDto<>(
                HttpStatus.OK.value(),
                true,
                dadosPessoa,
                "Dados retornados com sucesso"
            );
            return ResponseEntity.ok(response);
        } catch (TokenJwtInvalidoException e) {
            ApiResponseDto<DadosPessoaDto> response = new ApiResponseDto<>(
                HttpStatus.UNAUTHORIZED.value(),
                false,
                null,
                "Token inválido ou expirado"
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponseDto<DadosPessoaDto> response = new ApiResponseDto<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                null,
                "Erro ao buscar dados da pessoa: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
