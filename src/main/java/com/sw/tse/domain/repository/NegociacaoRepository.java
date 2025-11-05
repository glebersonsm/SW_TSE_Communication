package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.Negociacao;

@Repository
public interface NegociacaoRepository extends JpaRepository<Negociacao, Long> {
}

