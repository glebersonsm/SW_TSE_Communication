package com.sw.tse.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.EscolhaPeriodoModeloCota;

@Repository
public interface EscolhaPeriodoModeloCotaRepository extends JpaRepository<EscolhaPeriodoModeloCota, Long> {

        @Query("""
                        SELECT ep.inicioPeriodo
                        FROM EscolhaPeriodoModeloCota ep
                        WHERE ep.modeloCota.id IN :idsModeloCota
                        AND ep.ativo = true
                        AND ep.ano = :ano
                        AND ep.inicioPeriodo >= :dataReferencia
                        """)
        List<LocalDateTime> findDatasAberturaCalendario(
                        @Param("idsModeloCota") List<Long> idsModeloCota,
                        @Param("ano") Integer ano,
                        @Param("dataReferencia") LocalDateTime dataReferencia);

        List<EscolhaPeriodoModeloCota> findByModeloCotaIdAndAtivoTrueOrderByInicioPeriodoAsc(Long idModeloCota);
}
