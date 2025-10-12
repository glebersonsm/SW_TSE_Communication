package com.sw.tse.domain.service.interfaces;

import java.util.Optional;

import com.sw.tse.domain.model.db.ParametroFinanceiro;

public interface ParametroFinanceiroService {
    
    /**
     * Busca parâmetro financeiro por empresa
     * 
     * @param idEmpresa ID da empresa
     * @return Optional com o parâmetro financeiro encontrado
     */
    Optional<ParametroFinanceiro> buscarPorEmpresa(Long idEmpresa);
}
