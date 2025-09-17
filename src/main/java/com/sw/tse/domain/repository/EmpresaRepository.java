package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sw.tse.domain.model.db.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
}