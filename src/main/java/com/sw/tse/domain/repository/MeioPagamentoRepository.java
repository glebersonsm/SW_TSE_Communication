package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.MeioPagamento;

@Repository
public interface MeioPagamentoRepository extends JpaRepository<MeioPagamento, Long> {
    
    Optional<MeioPagamento> findFirstByCodMeioPagamento(String codMeioPagamento);
}

