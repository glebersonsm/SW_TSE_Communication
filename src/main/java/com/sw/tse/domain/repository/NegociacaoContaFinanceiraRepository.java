package com.sw.tse.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.model.db.NegociacaoContaFinanceira;

@Repository
public interface NegociacaoContaFinanceiraRepository extends JpaRepository<NegociacaoContaFinanceira, Long> {
    
    List<NegociacaoContaFinanceira> findByNegociacaoId(Long idNegociacao);
    
    List<NegociacaoContaFinanceira> findByContaFinanceiraId(Long idContaFinanceira);
    
    @Query("SELECT ncf FROM NegociacaoContaFinanceira ncf " +
           "WHERE ncf.contaFinanceira = :contaFinanceira " +
           "AND ncf.tipoNegociacao = 1 " +
           "ORDER BY ncf.dataCadastro DESC")
    Optional<NegociacaoContaFinanceira> findUltimaPorContaFinanceiraEStatus(@Param("contaFinanceira") ContaFinanceira contaFinanceira);
}

