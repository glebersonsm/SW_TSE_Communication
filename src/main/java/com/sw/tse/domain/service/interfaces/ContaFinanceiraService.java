package com.sw.tse.domain.service.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;
import com.sw.tse.domain.model.dto.ContasPaginadasDto;

public interface ContaFinanceiraService {
    
    /**
     * Busca contas financeiras do cliente autenticado como DTO com filtros opcionais
     * 
     * @param vencimentoInicial Data inicial de vencimento (opcional)
     * @param vencimentoFinal Data final de vencimento (opcional)
     * @param status Status da conta: B (Paga), P (Em aberto), V (Vencida) (opcional)
     * @return Lista de contas financeiras filtradas
     */
    List<ContaFinanceiraClienteDto> buscarContasClienteDto(LocalDate vencimentoInicial, LocalDate vencimentoFinal, String status);
    
    /**
     * Busca contas financeiras do cliente autenticado com filtros e paginação
     * 
     * @param vencimentoInicial Data inicial de vencimento (opcional)
     * @param vencimentoFinal Data final de vencimento (opcional)
     * @param status Status da conta: B (Paga), P (Em aberto), V (Vencida) (opcional)
     * @param empresaId ID da empresa para filtrar (opcional)
     * @param numeroDaPagina Número da página (inicia em 1)
     * @param quantidadeRegistrosRetornar Quantidade de registros por página
     * @return DTO com lista paginada e informações de paginação
     */
    ContasPaginadasDto buscarContasClienteDtoComPaginacao(
        LocalDate vencimentoInicial, 
        LocalDate vencimentoFinal, 
        String status,
        Long empresaId,
        Integer numeroDaPagina,
        Integer quantidadeRegistrosRetornar
    );
    
    /**
     * Gera segunda via de boleto em PDF
     */
    byte[] gerarSegundaViaBoleto(Long idContaFinanceira);
}
