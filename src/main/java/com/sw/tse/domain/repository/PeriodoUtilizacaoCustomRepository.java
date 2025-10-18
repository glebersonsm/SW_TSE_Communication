package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class PeriodoUtilizacaoCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Busca períodos de utilização disponíveis para reserva baseado na consulta SQL original
     * 
     * @param idContrato ID do contrato base para a consulta
     * @param ano Ano para filtrar os períodos (opcional - se null, busca todos os anos)
     * @param antecedenciaMinimaDias Período mínimo de antecedência para reserva
     * @param rciDiasMinimos Dias mínimos para RCI
     * @param poolDiaLimite Dia limite para pool
     * @param poolMesLimite Mês limite para pool
     * @param intercambiadoraRciId ID da intercambiadora RCI
     * @return Lista de períodos disponíveis com informações completas
     */
    public List<Object[]> buscarPeriodosDisponiveisParaReserva(
            Long idContrato,
            Integer ano,
            Integer antecedenciaMinimaDias,
            Integer rciDiasMinimos,
            Integer poolDiaLimite,
            Integer poolMesLimite,
            Long intercambiadoraRciId) {

        String sql = """
            SELECT 
                pe.idperiodoutilizacao,
                pe.descricaoperiodo,
                CAST((lpad(cast(pe.diainicio AS VARCHAR), 2, '0') || '/' || lpad(cast(pe.mesinicio AS VARCHAR), 2, '0') || '/' || pe.anoinicio) AS DATE) AS checkin,
                CAST((lpad(cast(pe.diafim AS VARCHAR), 2, '0') || '/' || lpad(cast(pe.mesfim AS VARCHAR), 2, '0') || '/' || pe.anofim) AS DATE) AS checkout,
                tp.idtipoperiodoutilizacao,
                tp.descricao AS descricaotipoperiodo,
                pe.anoinicio AS ano,
                (case when CAST((lpad(cast(pe.diainicio AS VARCHAR), 2, '0') || '/' || lpad(cast(pe.mesinicio AS VARCHAR), 2, '0') || '/' || pe.anoinicio) AS DATE) - CURRENT_DATE >= :antecedenciaMinima THEN 1 ELSE 0 END) AS reserva,
                (CASE WHEN rci.rci > 0 AND rci.datamin <= CAST((lpad(cast(pe.diainicio AS VARCHAR), 2, '0') || '/' || lpad(cast(pe.mesinicio AS VARCHAR), 2, '0') || '/' || pe.anoinicio) AS DATE) THEN 1 ELSE 0 END) AS RCI,
                (CASE WHEN pe.anoinicio > EXTRACT(YEAR FROM CURRENT_DATE) AND CAST(CURRENT_DATE AS DATE) <= CAST((lpad(CAST(:poolDiaLimite AS VARCHAR), 2, '0') || '/' || lpad(CAST(:poolMesLimite AS VARCHAR), 2, '0') || '/' || EXTRACT(YEAR FROM CURRENT_DATE)) AS DATE) THEN 1 ELSE 0 END) AS pool
            FROM 
                periodoutilizacao pe
            INNER JOIN
                periodogrupocota pgc ON pgc.idperiodoutilizacao = pe.idperiodoutilizacao AND pgc.idgrupocota IN (SELECT gc.idgrupocota FROM grupocota gc LEFT JOIN modelocota mc ON mc.idgrupocota = gc.idgrupocota LEFT JOIN cotauh co ON co.idmodelocota = mc.idmodelocota INNER JOIN contrato ct ON ct.idcotaadquirida = co.idcotauh AND ct.idcontrato = :idContrato)
            LEFT JOIN  
                tipoperiodoutilizacao tp ON tp.idtipoperiodoutilizacao = pe.idtipoperiodoutilizacao
            LEFT JOIN
                (SELECT COUNT(1) AS rci, CAST(current_date AS DATE)+:rciDiasMinimos datamin FROM contratointercambio WHERE idcontrato = :idContrato AND tipohistorico = 'ATIVO' and idintercambiadora = :intercambiadoraRciId) rci ON 1=1
            LEFT JOIN 
                (SELECT pu.anoinicio, pu.idtipoperiodoutilizacao, (MAX(mctp.qtdmaximautilizacoes) -COUNT(1)) AS saldo FROM periodosmodelocota pmc INNER JOIN cotauh co ON co.idmodelocota = pmc.idmodelocota LEFT JOIN periodoutilizacao pu ON pu.idperiodoutilizacao = pmc.idperiodoutilizacao LEFT JOIN modelocotatipoperiodo mctp ON mctp.idmodelocota = co.idmodelocota AND mctp.idtipoperiodoutilizacao = pu.idtipoperiodoutilizacao WHERE co.idcotauh = (SELECT idcotaadquirida FROM contrato WHERE idcontrato = :idContrato) AND pmc.idunidadehoteleira = co.idunidadehoteleira AND pmc.deletado = FALSE GROUP BY 1,2) periodospermitidos ON periodospermitidos.anoinicio = pe.anoinicio AND periodospermitidos.idtipoperiodoutilizacao = pe.idtipoperiodoutilizacao
            WHERE 
                NOT EXISTS 
                (
                    SELECT 
                        1 
                    FROM 
                        periodosmodelocota pm 
                    WHERE 
                        pm.idperiodoutilizacao = pe.idperiodoutilizacao AND
                        pm.deletado = false and
                        pm.idunidadehoteleira IN
                        (
                            SELECT 
                                uh.idunidadehoteleira 
                            FROM 
                                unidadehoteleira uh 
                            LEFT JOIN 
                                cotauh co ON co.idunidadehoteleira = uh.idunidadehoteleira 
                            LEFT JOIN 
                                contrato ct ON ct.idcotaadquirida = co.idcotauh 
                            WHERE 
                                ct.idcontrato = :idContrato
                        )
                ) AND tp.idtipoperiodoutilizacao 
                IN
                (
                    SELECT 
                        mctp.idtipoperiodoutilizacao 
                    FROM 
                        modelocotatipoperiodo mctp 
                    LEFT JOIN 
                        modelocota mc ON mc.idmodelocota = mctp.idmodelocota 
                    LEFT JOIN 
                        cotauh co ON co.idmodelocota = mc.idmodelocota 
                    LEFT JOIN 
                        contrato ct ON ct.idcotaadquirida = co.idcotauh 
                    WHERE 
                        ct.idcontrato = :idContrato
                ) AND NOT EXISTS 
                (
                    SELECT 
                        1 
                    FROM 
                        (
                            SELECT 
                                mctp.idtipoperiodoutilizacao,
                                coalesce(dados.ano,0) AS ano,
                                coalesce(dados.qtde,0) AS qtdesemanaescolhida,
                                mctp.qtdmaximautilizacoes,
                                (mctp.qtdmaximautilizacoes - coalesce(dados.qtde,0)) As qtdeperiododireito 
                            FROM 
                                contrato ct 
                            LEFT JOIN 
                                cotauh co ON co.idcotauh = ct.idcotaadquirida 
                            LEFT JOIN 
                                modelocota mc ON mc.idmodelocota = co.idmodelocota 
                            LEFT JOIN 
                                modelocotatipoperiodo mctp ON mctp.idmodelocota = mc.idmodelocota
                            LEFT JOIN 
                                (
                                    SELECT 
                                        tpu.idtipoperiodoutilizacao,
                                        pmc.idmodelocota,
                                        EXTRACT(YEAR FROM pmc.datainicial) AS ano,
                                        pmc.idcontrato,
                                        COUNT(1) AS qtde 
                                    FROM 
                                        periodosmodelocota pmc 
                                    LEFT JOIN 
                                        periodoutilizacao pu ON pu.idperiodoutilizacao = pmc.idperiodoutilizacao 
                                    LEFT JOIN 
                                        tipoperiodoutilizacao tpu ON tpu.idtipoperiodoutilizacao = pu.idtipoperiodoutilizacao 
                                    WHERE 
                                        pmc.deletado = FALSE AND 
                                        EXTRACT(YEAR FROM pmc.datainicial) >= EXTRACT(YEAR FROM CURRENT_DATE) 
                                    GROUP BY 
                                        tpu.idtipoperiodoutilizacao,
                                        pmc.idmodelocota,
                                        EXTRACT(YEAR FROM pmc.datainicial),
                                        pmc.idcontrato ORDER BY ano
                                ) dados 
                                    ON dados.idmodelocota = mctp.idmodelocota AND 
                                    dados.idtipoperiodoutilizacao = mctp.idtipoperiodoutilizacao AND ct.idcontrato = dados.idcontrato
                                WHERE 
                                    ct.idcontrato = :idContrato
                            ORDER BY ano
                        ) semanasescolhidas 
                    WHERE 
                        semanasescolhidas.idtipoperiodoutilizacao = tp.idtipoperiodoutilizacao AND 
                        semanasescolhidas.qtdeperiododireito = 0 AND 
                        semanasescolhidas.ano = pe.anoinicio
                ) AND
                    pe.habilitado and
                    CAST((lpad(cast(pe.diainicio AS VARCHAR), 2, '0') || '/' || lpad(cast(pe.mesinicio AS VARCHAR), 2, '0') || '/' || pe.anoinicio) AS DATE) > current_date and
                    (:ano IS NULL OR pe.anoinicio = :ano) and 
                pe.anoinicio IN(SELECT ep.ano+1 FROM escolhaperiodomodelocota ep
                LEFT JOIN cotauh co ON co.idmodelocota = ep.idmodelocota 
                LEFT JOIN contrato ct ON ct.idcotaadquirida = co.idcotauh 
                WHERE ct.idcontrato = :idContrato AND ativo AND CURRENT_DATE BETWEEN inicioperiodo AND fimperiodo) and
                (periodospermitidos.saldo > 0 OR periodospermitidos.saldo IS NULL)
            ORDER BY 
                1
            """;

        Query query = entityManager.createNativeQuery(sql);
        
        query.setParameter("idContrato", idContrato);
        query.setParameter("ano", ano);
        query.setParameter("antecedenciaMinima", antecedenciaMinimaDias);
        query.setParameter("rciDiasMinimos", rciDiasMinimos);
        query.setParameter("poolDiaLimite", poolDiaLimite);
        query.setParameter("poolMesLimite", poolMesLimite);
        query.setParameter("intercambiadoraRciId", intercambiadoraRciId);

        @SuppressWarnings("unchecked")
        List<Object[]> resultados = query.getResultList();
        
        return resultados;
    }

    /**
     * Verifica se um período específico ainda está disponível para reserva
     * (dupla checagem antes de criar a reserva)
     * 
     * @param idContrato ID do contrato
     * @param idPeriodoUtilizacao ID do período de utilização a validar
     * @return true se o período está disponível, false caso contrário
     */
    public boolean verificarPeriodoDisponivel(
            Long idContrato,
            Long idPeriodoUtilizacao) {

        String sql = """
            SELECT 
                COUNT(1)
            FROM 
                periodoutilizacao pe
            INNER JOIN
                periodogrupocota pgc ON pgc.idperiodoutilizacao = pe.idperiodoutilizacao 
                AND pgc.idgrupocota IN (
                    SELECT gc.idgrupocota 
                    FROM grupocota gc 
                    LEFT JOIN modelocota mc ON mc.idgrupocota = gc.idgrupocota 
                    LEFT JOIN cotauh co ON co.idmodelocota = mc.idmodelocota 
                    INNER JOIN contrato ct ON ct.idcotaadquirida = co.idcotauh 
                    WHERE ct.idcontrato = :idContrato
                )
            LEFT JOIN  
                tipoperiodoutilizacao tp ON tp.idtipoperiodoutilizacao = pe.idtipoperiodoutilizacao
            LEFT JOIN 
                (
                    SELECT pu.anoinicio, pu.idtipoperiodoutilizacao, (MAX(mctp.qtdmaximautilizacoes) - COUNT(1)) AS saldo 
                    FROM periodosmodelocota pmc 
                    INNER JOIN cotauh co ON co.idmodelocota = pmc.idmodelocota 
                    LEFT JOIN periodoutilizacao pu ON pu.idperiodoutilizacao = pmc.idperiodoutilizacao 
                    LEFT JOIN modelocotatipoperiodo mctp ON mctp.idmodelocota = co.idmodelocota 
                        AND mctp.idtipoperiodoutilizacao = pu.idtipoperiodoutilizacao 
                    WHERE co.idcotauh = (SELECT idcotaadquirida FROM contrato WHERE idcontrato = :idContrato) 
                      AND pmc.idunidadehoteleira = co.idunidadehoteleira 
                      AND pmc.deletado = FALSE 
                    GROUP BY 1,2
                ) periodospermitidos ON periodospermitidos.anoinicio = pe.anoinicio 
                    AND periodospermitidos.idtipoperiodoutilizacao = pe.idtipoperiodoutilizacao
            WHERE 
                pe.idperiodoutilizacao = :idPeriodoUtilizacao 
                AND pe.habilitado = true
                AND CAST((lpad(cast(pe.diainicio AS VARCHAR), 2, '0') || '/' || 
                         lpad(cast(pe.mesinicio AS VARCHAR), 2, '0') || '/' || 
                         pe.anoinicio) AS DATE) > CURRENT_DATE
                AND NOT EXISTS (
                    SELECT 1 
                    FROM periodosmodelocota pm 
                    WHERE pm.idperiodoutilizacao = pe.idperiodoutilizacao 
                      AND pm.deletado = false 
                      AND pm.idunidadehoteleira IN (
                          SELECT uh.idunidadehoteleira 
                          FROM unidadehoteleira uh 
                          LEFT JOIN cotauh co ON co.idunidadehoteleira = uh.idunidadehoteleira 
                          LEFT JOIN contrato ct ON ct.idcotaadquirida = co.idcotauh 
                          WHERE ct.idcontrato = :idContrato
                      )
                )
                AND tp.idtipoperiodoutilizacao IN (
                    SELECT mctp.idtipoperiodoutilizacao 
                    FROM modelocotatipoperiodo mctp 
                    LEFT JOIN modelocota mc ON mc.idmodelocota = mctp.idmodelocota 
                    LEFT JOIN cotauh co ON co.idmodelocota = mc.idmodelocota 
                    LEFT JOIN contrato ct ON ct.idcotaadquirida = co.idcotauh 
                    WHERE ct.idcontrato = :idContrato
                )
                AND NOT EXISTS (
                    SELECT 1 
                    FROM (
                        SELECT 
                            mctp.idtipoperiodoutilizacao,
                            coalesce(dados.ano,0) AS ano,
                            coalesce(dados.qtde,0) AS qtdesemanaescolhida,
                            mctp.qtdmaximautilizacoes,
                            (mctp.qtdmaximautilizacoes - coalesce(dados.qtde,0)) As qtdeperiododireito 
                        FROM contrato ct 
                        LEFT JOIN cotauh co ON co.idcotauh = ct.idcotaadquirida 
                        LEFT JOIN modelocota mc ON mc.idmodelocota = co.idmodelocota 
                        LEFT JOIN modelocotatipoperiodo mctp ON mctp.idmodelocota = mc.idmodelocota
                        LEFT JOIN (
                            SELECT 
                                tpu.idtipoperiodoutilizacao,
                                pmc.idmodelocota,
                                EXTRACT(YEAR FROM pmc.datainicial) AS ano,
                                pmc.idcontrato,
                                COUNT(1) AS qtde 
                            FROM periodosmodelocota pmc 
                            LEFT JOIN periodoutilizacao pu ON pu.idperiodoutilizacao = pmc.idperiodoutilizacao 
                            LEFT JOIN tipoperiodoutilizacao tpu ON tpu.idtipoperiodoutilizacao = pu.idtipoperiodoutilizacao 
                            WHERE pmc.deletado = FALSE 
                              AND EXTRACT(YEAR FROM pmc.datainicial) >= EXTRACT(YEAR FROM CURRENT_DATE) 
                            GROUP BY tpu.idtipoperiodoutilizacao, pmc.idmodelocota,
                                     EXTRACT(YEAR FROM pmc.datainicial), pmc.idcontrato 
                            ORDER BY ano
                        ) dados ON dados.idmodelocota = mctp.idmodelocota 
                            AND dados.idtipoperiodoutilizacao = mctp.idtipoperiodoutilizacao 
                            AND ct.idcontrato = dados.idcontrato
                        WHERE ct.idcontrato = :idContrato
                        ORDER BY ano
                    ) semanasescolhidas 
                    WHERE semanasescolhidas.idtipoperiodoutilizacao = tp.idtipoperiodoutilizacao 
                      AND semanasescolhidas.qtdeperiododireito = 0 
                      AND semanasescolhidas.ano = pe.anoinicio
                )
                AND pe.anoinicio IN (
                    SELECT ep.ano+1 
                    FROM escolhaperiodomodelocota ep
                    LEFT JOIN cotauh co ON co.idmodelocota = ep.idmodelocota 
                    LEFT JOIN contrato ct ON ct.idcotaadquirida = co.idcotauh 
                    WHERE ct.idcontrato = :idContrato 
                      AND ativo 
                      AND CURRENT_DATE BETWEEN inicioperiodo AND fimperiodo
                )
                AND (periodospermitidos.saldo > 0 OR periodospermitidos.saldo IS NULL)
            """;

        Query query = entityManager.createNativeQuery(sql);
        
        query.setParameter("idContrato", idContrato);
        query.setParameter("idPeriodoUtilizacao", idPeriodoUtilizacao);

        Long count = ((Number) query.getSingleResult()).longValue();
        
        return count > 0;
    }
}
