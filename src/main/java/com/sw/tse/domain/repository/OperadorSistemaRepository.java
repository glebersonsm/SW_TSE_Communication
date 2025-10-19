package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.OperadorSistema;

@Repository
public interface OperadorSistemaRepository extends JpaRepository<OperadorSistema, Long> {
    
    /**
     * Busca operador sistema por ID da pessoa
     */
    Optional<OperadorSistema> findByPessoa_IdPessoa(Long idPessoa);
}

