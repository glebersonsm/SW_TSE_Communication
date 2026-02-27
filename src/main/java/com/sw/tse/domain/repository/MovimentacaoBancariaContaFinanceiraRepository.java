package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.MovimentacaoBancariaContaFinanceira;

@Repository
public interface MovimentacaoBancariaContaFinanceiraRepository
        extends JpaRepository<MovimentacaoBancariaContaFinanceira, Long> {

    /** Verifica se já existe vínculo da conta com alguma MovimentacaoBancaria. */
    List<MovimentacaoBancariaContaFinanceira> findByContaFinanceiraId(Long idContaFinanceira);
}
