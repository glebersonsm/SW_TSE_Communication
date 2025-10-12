package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.Banco;

@Repository
public interface BancoRepository extends JpaRepository<Banco, Long> {
}
