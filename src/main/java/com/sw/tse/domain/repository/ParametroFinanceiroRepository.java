package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ParametroFinanceiro;

@Repository
public interface ParametroFinanceiroRepository extends JpaRepository<ParametroFinanceiro, Long> {
    
    /**
     * Busca parâmetro financeiro por empresa
     * 
     * @param idEmpresa ID da empresa
     * @return Optional com o parâmetro financeiro encontrado
     */
    Optional<ParametroFinanceiro> findByEmpresaId(Long idEmpresa);
}
