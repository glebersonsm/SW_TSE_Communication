package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ConvencaoSistema;

@Repository
public interface ConvencaoSistemaRepository extends JpaRepository<ConvencaoSistema, Long> {

    @Query("SELECT cs FROM ConvencaoSistema cs WHERE cs.grupo = :grupo ORDER BY cs.descricao ASC")
    List<ConvencaoSistema> findByGrupo(@Param("grupo") String grupo);
}
