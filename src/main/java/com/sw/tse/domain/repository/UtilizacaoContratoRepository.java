package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.UtilizacaoContrato;

@Repository
public interface UtilizacaoContratoRepository extends JpaRepository<UtilizacaoContrato, Long> {
    
    @Query("""
        SELECT u FROM UtilizacaoContrato u
        WHERE u.contrato.id IN (
            SELECT c.id FROM Contrato c 
            WHERE c.pessoaCessionario.id = :idPessoaCliente 
            OR c.pessaoCocessionario.id = :idPessoaCliente
        )
        AND YEAR(u.dataCheckin) = :ano
        AND u.status != 'CANCELADO'
        AND u.tipoUtilizacaoContrato.sigla IN ('RESERVA', 'RCI', 'POOL')
        ORDER BY u.dataCheckin ASC
        """)
    List<UtilizacaoContrato> findUtilizacoesPorAnoECliente(
        @Param("ano") int ano,
        @Param("idPessoaCliente") Long idPessoaCliente
    );
}
