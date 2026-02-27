package com.sw.tse.api.controller;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ProcessamentoPagamentoResponseDto;
import com.sw.tse.api.dto.ProcessarPagamentoAprovadoTseDto;
import com.sw.tse.core.context.FeriadosContext;
import com.sw.tse.domain.expection.PagamentoTseBusinessException;
import com.sw.tse.domain.expection.RegraDeNegocioException;
import com.sw.tse.domain.service.interfaces.ReprocessamentoPagamentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoint de reprocessamento idempotente de pagamentos no TSE.
 * Diferente do /processar-aprovado (que processa pela primeira vez),
 * este endpoint verifica o estado atual e reconstrói apenas o que está
 * incompleto.
 */
@Slf4j
@RestController
@RequestMapping("/api/pagamento-cartao")
@RequiredArgsConstructor
@Tag(name = "Pagamento Cartão", description = "Endpoints para processamento de pagamentos com cartão")
public class ReprocessamentoTseController {

    private final ReprocessamentoPagamentoService reprocessamentoService;

    @PostMapping("/verificar-e-sincronizar")
    @Operation(summary = "Verificar e sincronizar pagamento", description = "Verifica o estado atual da transação no TSE e reconstrói apenas o que estiver faltando (idempotente). "
            +
            "Suporta PIX (1 conta e múltiplas contas) e Cartão.")
    public ResponseEntity<ProcessamentoPagamentoResponseDto> verificarESincronizar(
            @RequestHeader("Authorization") String token,
            @RequestBody ProcessarPagamentoAprovadoTseDto dto) {

        log.info("[Reprocessamento] Recebida requisição. IdTransacao={}, MeioPagamento={}, Contas={}",
                dto.getIdTransacao(), dto.getMeioPagamento(),
                dto.getContasFinanceiras() != null ? dto.getContasFinanceiras().size() : 0);

        try {
            // Propaga lista de feriados para DiasAtrasoHelper via contexto de thread
            FeriadosContext.setFeriados(
                    dto.getFeriados() != null ? dto.getFeriados() : Collections.emptyList());

            ProcessamentoPagamentoResponseDto response = reprocessamentoService.verificarESincronizar(dto);

            log.info("[Reprocessamento] Concluído com sucesso. IdNegociacao={}, Status={}",
                    response.getIdNegociacao(), response.getStatus());

            return ResponseEntity.ok(response);

        } catch (PagamentoTseBusinessException e) {
            log.warn("[Reprocessamento] Bloqueio de negócio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ProcessamentoPagamentoResponseDto.builder()
                            .mensagem(e.getMessage())
                            .status("BLOQUEADO_NEGOCIO")
                            .build());

        } catch (RegraDeNegocioException e) {
            log.warn("[Reprocessamento] Regra de negócio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ProcessamentoPagamentoResponseDto.builder()
                            .mensagem(e.getMessage())
                            .status("ERRO_NEGOCIO")
                            .build());

        } catch (Exception e) {
            log.error("[Reprocessamento] Erro inesperado para IdTransacao={}", dto.getIdTransacao(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ProcessamentoPagamentoResponseDto.builder()
                            .mensagem("Erro interno no reprocessamento: " + e.getMessage())
                            .status("ERRO")
                            .build());
        } finally {
            FeriadosContext.clear();
        }
    }
}
