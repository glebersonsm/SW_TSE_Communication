package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.MovimentacaoBancaria;

@Repository
public interface MovimentacaoBancariaRepository extends JpaRepository<MovimentacaoBancaria, Long> {
}

