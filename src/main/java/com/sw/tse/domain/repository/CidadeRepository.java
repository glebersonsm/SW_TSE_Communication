package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sw.tse.domain.model.db.Cidade;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {

	@Query("SELECT c FROM Cidade c " +
	           "WHERE UPPER(c.nome) = UPPER(:nome) " +
	           "AND UPPER(c.uf) = UPPER(:uf) " +
	           "ORDER BY c.id DESC")
    List<Cidade> findByNomeAndUfOrdenado(String nome, String uf);
}

