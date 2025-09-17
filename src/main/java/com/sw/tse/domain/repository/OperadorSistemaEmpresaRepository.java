package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.OperadorSistemaEmpresa;

public interface OperadorSistemaEmpresaRepository extends JpaRepository<OperadorSistemaEmpresa, Long> {
    List<OperadorSistemaEmpresa> findByOperadorSistema(OperadorSistema operadorSistema);
}