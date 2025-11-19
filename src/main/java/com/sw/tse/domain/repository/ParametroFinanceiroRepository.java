package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ParametroFinanceiro;

@Repository
public interface ParametroFinanceiroRepository extends JpaRepository<ParametroFinanceiro, Long> {
    
    /**
     * Busca parâmetro financeiro por empresa usando idtenant
     * 
     * @param idEmpresa ID da empresa (idtenant)
     * @return Optional com o parâmetro financeiro encontrado
     */
    @Query(value = "SELECT * FROM parametrizacaohistoricomovbancaria WHERE idtenant = :idTenant", nativeQuery = true)
    Optional<ParametroFinanceiro> findByEmpresaId(@Param("idTenant") Long idTenant);
}
