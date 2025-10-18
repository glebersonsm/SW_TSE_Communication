package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.api.dto.ContaFinanceiraClienteDto;

public interface ContaFinanceiraService {
    
    /**
     * Busca contas financeiras do cliente autenticado como DTO
     */
    List<ContaFinanceiraClienteDto> buscarContasClienteDto();
    
    /**
     * Gera segunda via de boleto em PDF
     */
    byte[] gerarSegundaViaBoleto(Long idContaFinanceira);
}
