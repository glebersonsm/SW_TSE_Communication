package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sw.tse.domain.model.db.GrupoCota;

public interface GrupoCotaRepository extends JpaRepository<GrupoCota, Long> {

    @Query("SELECT g FROM GrupoCota g LEFT JOIN FETCH g.empresa e ORDER BY g.descricao")
    List<GrupoCota> findAllWithEmpresaOrdered();
}
