package com.sw.tse.domain.service.interfaces;

import com.sw.tse.api.dto.ProcessarPagamentoAprovadoTseDto;

public interface PagamentoCartaoService {
    
    /**
     * Processa um pagamento aprovado no portal, criando transação, conta consolidada e negociação
     * @param dto Dados do pagamento aprovado
     */
    void processarPagamentoAprovado(ProcessarPagamentoAprovadoTseDto dto);
}

