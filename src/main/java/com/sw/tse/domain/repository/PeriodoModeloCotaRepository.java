package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.PeriodoModeloCota;

@Repository
public interface PeriodoModeloCotaRepository extends JpaRepository<PeriodoModeloCota, Long> {
    
    @Query("SELECT p.id FROM PeriodoModeloCota p WHERE p.id = " +
           "(SELECT u.periodoModeloCota.id FROM UtilizacaoContrato u WHERE u.id = :idUtilizacaoContrato)")
    List<Long> findIdsByUtilizacaoContratoId(@Param("idUtilizacaoContrato") Long idUtilizacaoContrato);
    
    @Query("SELECT COUNT(u) FROM UtilizacaoContrato u " +
           "WHERE u.periodoModeloCota.id = :idPeriodoModeloCota " +
           "AND u.status IN ('ATIVO', 'CONFIRMADO') " +
           "AND u.id != :idUtilizacaoContratoAtual")
    long countUtilizacoesAtivasByPeriodoModeloCota(
        @Param("idPeriodoModeloCota") Long idPeriodoModeloCota,
        @Param("idUtilizacaoContratoAtual") Long idUtilizacaoContratoAtual
    );

    boolean existsByPeriodoUtilizacaoIdAndUnidadeHoteleiraIdAndDeletadoFalse(Long idPeriodoUtilizacao, Long idUnidadeHoteleira);

    /**
     * Adquire uma trava consultiva (advisory lock) no nível de transação do PostgreSQL.
     * Esta trava é "cirúrgica": bloqueia apenas a combinação de UH e Período, 
     * impedindo concorrência de frações de segundo sem travar tabelas ou registros físicos.
     */
    @Query(value = "SELECT pg_advisory_xact_lock(hashtext(concat('reserva-', :idUh, '-', :idPeriodo)))", nativeQuery = true)
    void adquirirTravaNegocial(@Param("idUh") Long idUh, @Param("idPeriodo") Long idPeriodo);
}

