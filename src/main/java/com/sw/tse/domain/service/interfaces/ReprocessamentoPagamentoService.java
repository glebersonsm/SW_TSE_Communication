package com.sw.tse.domain.service.interfaces;

import com.sw.tse.api.dto.ProcessamentoPagamentoResponseDto;
import com.sw.tse.api.dto.ProcessarPagamentoAprovadoTseDto;

/**
 * Serviço responsável pelo reprocessamento idempotente de pagamentos no TSE.
 * Diferente do ProcessamentoPagamentoService (que processa pela primeira vez),
 * este serviço verifica o estado atual e reconstrói apenas o que está faltando.
 */
public interface ReprocessamentoPagamentoService {

    /**
     * Verifica o estado da transação no TSE e sincroniza o que estiver faltando.
     * Idempotente: pode ser chamado múltiplas vezes sem gerar duplicidade.
     *
     * Cenários:
     * - PIX 1 conta: valida baixa + cria Negociação/MovimentacaoBancaria se
     * ausentes
     * - PIX N contas sem consolidada: executa fluxo completo
     * - PIX N contas com consolidada: valida/cria Negociação e MovimentacaoBancaria
     * - Cartão: valida TransacaoDebitoCredito + cria Negociação se ausente
     */
    ProcessamentoPagamentoResponseDto verificarESincronizar(ProcessarPagamentoAprovadoTseDto dto);
}
