package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ContaMovimentacaoBancaria;

@Repository
public interface ContaMovimentacaoBancariaRepository extends JpaRepository<ContaMovimentacaoBancaria, Long> {

    /**
     * Busca contas de movimentação bancária por empresa
     */
    List<ContaMovimentacaoBancaria> findByEmpresaId(Long idEmpresa);

    /**
     * Busca contas de movimentação bancária por banco
     */
    List<ContaMovimentacaoBancaria> findByBancoId(Long idBanco);

    /**
     * Busca contas de movimentação bancária por status (ativa/inativa)
     */
    List<ContaMovimentacaoBancaria> findByInativa(Boolean inativa);

    /**
     * Busca contas de movimentação bancária por empresa e status
     */
    List<ContaMovimentacaoBancaria> findByEmpresaIdAndInativa(Long idEmpresa, Boolean inativa);
}
