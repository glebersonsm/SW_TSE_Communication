package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.TipoOrigemContaFinanceira;

@Repository
public interface TipoOrigemContaFinanceiraRepository extends JpaRepository<TipoOrigemContaFinanceira, Integer> {
    
    Optional<TipoOrigemContaFinanceira> findBySysId(String sysId);
}

