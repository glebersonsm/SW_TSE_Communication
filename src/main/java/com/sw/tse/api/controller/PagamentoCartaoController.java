package com.sw.tse.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sw.tse.api.dto.ProcessamentoPagamentoResponseDto;
import com.sw.tse.api.dto.ProcessarPagamentoAprovadoTseDto;
import com.sw.tse.domain.expection.BandeiraCartaoNaoEncontradaException;
import com.sw.tse.domain.expection.ContaFinanceiraNaoEncontradaException;
import com.sw.tse.domain.expection.OperadorSistemaNotFoundException;
import com.sw.tse.domain.expection.PagamentoCartaoException;
import com.sw.tse.domain.expection.PagamentoTseBusinessException;
import com.sw.tse.domain.service.interfaces.PagamentoCartaoService;
import com.sw.tse.core.context.FeriadosContext;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api/pagamento-cartao")
@RequiredArgsConstructor
@Tag(name = "Pagamento Cartão", description = "Endpoints para processamento de pagamentos com cartão")
public class PagamentoCartaoController {

        private final PagamentoCartaoService pagamentoCartaoService;

        @PostMapping("/processar-aprovado")
        @Operation(summary = "Processar pagamento aprovado", description = "Processa um pagamento aprovado no portal, criando transação, conta consolidada e negociação no TSE")
        public ResponseEntity<ProcessamentoPagamentoResponseDto> processarPagamentoAprovado(
                        @RequestHeader("Authorization") String token,
                        @RequestBody ProcessarPagamentoAprovadoTseDto dto) {

                log.info("Recebida requisição para processar pagamento aprovado. IdTransacao: {}, Valor: {}, Contas: {}",
                                dto.getIdTransacao(), dto.getValorTotal(), dto.getContasFinanceiras().size());

                try {
                        FeriadosContext.setFeriados(
                                        dto.getFeriados() != null ? dto.getFeriados() : Collections.emptyList());
                        ProcessamentoPagamentoResponseDto response = pagamentoCartaoService
                                        .processarPagamentoAprovado(dto);
                        log.info("Pagamento processado com sucesso no TSE Communication. IdNegociacao: {}",
                                        response.getIdNegociacao());
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                } catch (OperadorSistemaNotFoundException e) {
                        log.error("Operador sistema não encontrado", e);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(ProcessamentoPagamentoResponseDto.builder()
                                                        .mensagem("Usuário não encontrado: " + e.getMessage())
                                                        .status("ERRO").build());
                } catch (ContaFinanceiraNaoEncontradaException e) {
                        log.error("Contas financeiras não encontradas", e);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(ProcessamentoPagamentoResponseDto.builder()
                                                        .mensagem("Contas financeiras não encontradas: "
                                                                        + e.getMessage())
                                                        .status("ERRO").build());
                } catch (BandeiraCartaoNaoEncontradaException e) {
                        log.warn("Configuração de bandeira de cartão não encontrada: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                        .body(ProcessamentoPagamentoResponseDto.builder()
                                                        .mensagem(e.getMessage())
                                                        .status("ERRO_CONFIGURACAO").build());
                } catch (PagamentoCartaoException e) {
                        log.error("Erro de regra de negócio ao processar pagamento", e);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(ProcessamentoPagamentoResponseDto.builder()
                                                        .mensagem("Erro ao processar pagamento: " + e.getMessage())
                                                        .status("ERRO").build());
                } catch (PagamentoTseBusinessException e) {
                        log.warn("Erro de regra de negócio TSE ao processar pagamento aprovado: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                        .body(ProcessamentoPagamentoResponseDto.builder()
                                                        .mensagem(e.getMessage())
                                                        .status("ERRO_NEGOCIO").build());
                } catch (Exception e) {
                        log.error("Erro inesperado ao processar pagamento aprovado", e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ProcessamentoPagamentoResponseDto.builder()
                                                        .mensagem("Erro interno ao processar pagamento: "
                                                                        + e.getMessage())
                                                        .status("ERRO").build());
                } finally {
                        FeriadosContext.clear();
                }
        }
}
