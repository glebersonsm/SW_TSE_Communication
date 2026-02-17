package com.sw.tse.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
                        AND u.tipoUtilizacaoContrato.sigla IN ('RESERVA', 'DEPSEMANA', 'DEPPOOL')
                        ORDER BY u.dataCheckin ASC
                        """)
        List<UtilizacaoContrato> findUtilizacoesPorAnoECliente(
                        @Param("ano") int ano,
                        @Param("idPessoaCliente") Long idPessoaCliente);

        @Query("""
                        SELECT u FROM UtilizacaoContrato u
                        WHERE u.contrato.id = :idContrato
                        AND u.contrato.id IN (
                            SELECT c.id FROM Contrato c
                            WHERE c.pessoaCessionario.id = :idPessoaCliente
                            OR c.pessaoCocessionario.id = :idPessoaCliente
                        )
                        AND YEAR(u.dataCheckin) = :ano
                        AND u.status != 'CANCELADO'
                        AND u.tipoUtilizacaoContrato.sigla IN ('RESERVA', 'RCI', 'POOL')
                        ORDER BY u.dataCheckin ASC
                        """)
        List<UtilizacaoContrato> findUtilizacoesPorContratoAnoECliente(
                        @Param("idContrato") Long idContrato,
                        @Param("ano") int ano,
                        @Param("idPessoaCliente") Long idPessoaCliente);

        @Query("""
                        SELECT MIN(u.dataCheckin)
                        FROM UtilizacaoContrato u
                        WHERE (u.contrato.pessoaCessionario.id = :idPessoa OR u.contrato.pessaoCocessionario.id = :idPessoa)
                        AND u.status IN ('ATIVO', 'CONFIRMADO')
                        AND u.tipoUtilizacaoContrato.sigla = 'RESERVA'
                        AND u.dataCheckin >= :dataInicio
                        """)
        Optional<LocalDateTime> findProximoCheckin(
                        @Param("idPessoa") Long idPessoa,
                        @Param("dataInicio") LocalDateTime dataInicio);
}
