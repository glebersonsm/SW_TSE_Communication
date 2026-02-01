package com.sw.tse.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ContaFinanceira;
import com.sw.tse.domain.model.dto.InadimplenciaDto;

@Repository
public interface ContaFinanceiraRepository extends JpaRepository<ContaFinanceira, Long> {
    
    /**
     * Calcula o valor integralizado de um contrato
     * REEMBOLSO é tratado como negativo (desconto), pois é devolução ao cliente
     * 
     * @param idContrato ID do contrato
     * @return Valor total integralizado (soma dos valores positivos menos os reembolsos)
     */
    @Query("""
        SELECT COALESCE(SUM(
            CASE
                WHEN tc.sysId = 'REEMBOLSO' THEN -cf.valorParcela
                ELSE cf.valorParcela
            END
        ), 0)
        FROM ContaFinanceira cf
        JOIN cf.contrato ct
        JOIN cf.origemConta tc
        WHERE ct.id = :idContrato
          AND ct.status IN ('ATIVO', 'ATIVOREV')
          AND (
            (tc.sysId IN ('PARC','ENTRADA','INTERMEDIARIA','MULTARESCIS')
             AND (
               cf.tipoHistorico IN ('BAIXADO','TRANSFERIDO','BAIXADOCARTACREDITO')
               OR (cf.meioPagamento.id = 3 AND cf.tipoHistorico IN ('BAIXADO','ATIVO'))
               OR (cf.meioPagamento.id IN (8,11) AND cf.tipoHistorico IN ('BAIXADO','ATIVO') AND cf.recorrenciaAutorizada = TRUE)
               OR (cf.meioPagamento.id IN (10,12) AND LENGTH(cf.numeroDocumento) > 10)
             ))
            OR (tc.sysId = 'REEMBOLSO'
                AND (
                  cf.tipoHistorico IN ('BAIXADO','TRANSFERIDO','BAIXADOCARTACREDITO','ATIVO')
                ))
          )
    """)
    BigDecimal calcularValorIntegralizado(@Param("idContrato") Long idContrato);
    
    /**
     * Busca inadimplência de contrato (parcelas de contrato)
     * 
     * @param idContrato ID do contrato
     * @return DTO com quantidade e valor inadimplente
     */
    @Query("""
        SELECT new com.sw.tse.domain.model.dto.InadimplenciaDto(
            COUNT(cf.id),
            COALESCE(SUM(cf.valorParcela + cf.valorAcrescimoAcumuladoCorrecaoMonetaria + cf.valorAcrescimo), 0),
            'CONTRATO'
        )
        FROM ContaFinanceira cf
        JOIN cf.origemConta tocf
        WHERE cf.contrato.id = :idContrato
          AND cf.origemConta.idTipoOrigemContaFinanceira <> 15
          AND cf.tipoHistorico = 'ATIVO'
          AND cf.dataVencimento <= CURRENT_DATE - 2 DAY
          AND cf.destinoContaFinanceira = 'R'
          AND tocf.sysId IN ('PARC','ENTRADA','INTERMEDIARIA','MULTARESCIS')
          AND (
            (cf.meioPagamento.id NOT IN (1, 3, 8, 11))
            OR (cf.meioPagamento.id IN (8, 11) AND cf.recorrenciaAutorizada = FALSE)
          )
    """)
    Optional<InadimplenciaDto> buscarInadimplenciaContrato(@Param("idContrato") Long idContrato);
    
    /**
     * Busca inadimplência de condomínio (taxas condominiais)
     * 
     * @param idContrato ID do contrato
     * @return DTO com quantidade e valor inadimplente
     */
    @Query("""
        SELECT new com.sw.tse.domain.model.dto.InadimplenciaDto(
            COUNT(cf.id),
            COALESCE(SUM(cf.valorReceber), 0),
            'CONDOMINIO'
        )
        FROM ContaFinanceira cf
        WHERE cf.contrato.idContratoOrigemAdm = :idContrato
          AND cf.origemConta.idTipoOrigemContaFinanceira = 15
          AND COALESCE(cf.dataVencimentoOriginal, cf.dataVencimento) < CURRENT_DATE - 1 DAY
          AND cf.meioPagamento.id NOT IN (3, 8, 11)
          AND cf.destinoContaFinanceira = 'R'
          AND cf.tipoHistorico = 'ATIVO'
          AND cf.valorParcela > 0
    """)
    Optional<InadimplenciaDto> buscarInadimplenciaCondominio(@Param("idContrato") Long idContrato);
    
    // ========== MÉTODOS PARA CONTAFINANCEIRASERVICE ==========
    
    /**
     * Busca contas financeiras por contrato
     */
    List<ContaFinanceira> findByContratoId(Long idContrato);
    
    /**
     * Busca contas financeiras por pessoa
     */
    List<ContaFinanceira> findByPessoaIdPessoa(Long idPessoa);
    
    /**
     * Busca contas financeiras por status de pagamento
     */
    List<ContaFinanceira> findByPago(Boolean pago);
    
    /**
     * Busca contas financeiras por tipo de histórico
     */
    List<ContaFinanceira> findByTipoHistorico(String tipoHistorico);
    
    /**
     * Busca contas financeiras por destino
     */
    List<ContaFinanceira> findByDestinoContaFinanceira(String destino);
    
    /**
     * Busca contas financeiras por empresa
     */
    List<ContaFinanceira> findByEmpresaId(Long idEmpresa);
    
    /**
     * Busca contas financeiras por período de vencimento
     */
    List<ContaFinanceira> findByDataVencimentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    
    /**
     * Busca contas financeiras por período de pagamento
     */
    List<ContaFinanceira> findByDataPagamentoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    
    /**
     * Busca contas em atraso
     */
    @Query("SELECT cf FROM ContaFinanceira cf WHERE cf.dataVencimento < :dataReferencia AND cf.pago = false")
    List<ContaFinanceira> findContasEmAtraso(@Param("dataReferencia") LocalDateTime dataReferencia);
    
    /**
     * Conta quantas contas não foram pagas
     */
    @Query("SELECT COUNT(cf) FROM ContaFinanceira cf WHERE cf.pago = false")
    Long countContasNaoPagas();
    
    /**
     * Soma valores das contas não pagas
     */
    @Query("SELECT SUM(cf.valorParcela) FROM ContaFinanceira cf WHERE cf.pago = false")
    Double sumValoresContasNaoPagas();
    
    /**
     * Busca contas por cliente
     * Considera: idpessoa direto na conta OU cessionário/co-cessionário do contrato
     */
    @Query("""
        SELECT cf FROM ContaFinanceira cf 
        LEFT JOIN cf.contrato ct
        JOIN cf.origemConta toc
        WHERE (
            cf.pessoa.idPessoa = :idCliente
            OR ct.pessoaCessionario.idPessoa = :idCliente
            OR ct.pessaoCocessionario.idPessoa = :idCliente
          )
          AND (
            toc.sysId IN ('ENTRADA', 'INTERMEDIARIA', 'MULTARESCIS', 'PARC', 'REEMBOLSO')
            OR toc.idTipoOrigemContaFinanceira = 15
          )
    """)
    List<ContaFinanceira> findContasPorCliente(@Param("idCliente") Long idCliente);
    
    /**
     * Busca contas por cliente com filtros opcionais
     * 
     * @param idCliente ID do cliente
     * @param vencimentoInicial Data inicial de vencimento (opcional)
     * @param vencimentoFinal Data final de vencimento (opcional)
     * @param status Status: B (Paga), P (Em aberto), V (Vencida)
     * @return Lista de contas filtradas
     */
    @Query(value = """
        SELECT * 
        FROM contafinanceira cf 
        LEFT JOIN contrato ct ON cf.idcontrato = ct.idcontrato
        INNER JOIN tipoorigemcontafinanceira toc ON cf.idorigemconta = toc.idtipoorigemcontafinanceira
        WHERE (
            -- Buscar por idpessoa direto na conta financeira (pode ser NULL)
            cf.idpessoa = :idCliente
            -- OU buscar por cessionário do contrato
            OR ct.idpessoacessionario = :idCliente
            -- OU buscar por co-cessionário do contrato
            OR ct.idpessoacocessionario = :idCliente
          )
          AND cf.tipohistorico NOT IN ('RENEGOCIADA', 'EXCLUIDO', 'CANCELADO')
          AND (
            toc.sysid IN ('ENTRADA', 'INTERMEDIARIA', 'MULTARESCIS', 'PARC', 'REEMBOLSO')
            OR toc.idtipoorigemcontafinanceira = 15
          )
          AND (CAST(:vencimentoInicial AS date) IS NULL OR DATE(cf.datavencimento) >= CAST(:vencimentoInicial AS date))
          AND (CAST(:vencimentoFinal AS date) IS NULL OR DATE(cf.datavencimento) <= CAST(:vencimentoFinal AS date))
          AND (
            :status IS NULL 
            OR (:status = 'B' AND (
                cf.pago = TRUE 
                OR UPPER(cf.destinocontafinanceira) = 'P'
              ))
            OR (:status = 'P' AND cf.pago = FALSE 
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.valorreceber > 0
                AND cf.datavencimento >= CURRENT_DATE)
            OR (:status = 'V' AND cf.pago = FALSE 
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.valorreceber > 0 
                AND cf.datavencimento < CURRENT_DATE)
          )
        ORDER BY cf.datavencimento ASC
    """, nativeQuery = true)
    List<ContaFinanceira> findContasPorClienteComFiltros(
        @Param("idCliente") Long idCliente,
        @Param("vencimentoInicial") LocalDate vencimentoInicial,
        @Param("vencimentoFinal") LocalDate vencimentoFinal,
        @Param("status") String status
    );
    
    /**
     * Conta total de registros filtrados para paginação
     */
    @Query(value = """
        SELECT COUNT(*) 
        FROM contafinanceira cf 
        LEFT JOIN meiopagamento mp ON cf.idmeiopagamento = mp.idmeiopagamento
        LEFT JOIN contrato ct ON cf.idcontrato = ct.idcontrato
        INNER JOIN tipoorigemcontafinanceira toc ON cf.idorigemconta = toc.idtipoorigemcontafinanceira
        WHERE (
            -- Buscar por idpessoa direto na conta financeira (pode ser NULL)
            cf.idpessoa = :idCliente
            -- OU buscar por cessionário do contrato
            OR ct.idpessoacessionario = :idCliente
            -- OU buscar por co-cessionário do contrato
            OR ct.idpessoacocessionario = :idCliente
          )
          AND cf.tipohistorico NOT IN ('RENEGOCIADA', 'EXCLUIDO', 'CANCELADO')
          AND (
            toc.sysid IN ('ENTRADA', 'INTERMEDIARIA', 'MULTARESCIS', 'PARC', 'REEMBOLSO')
            OR toc.idtipoorigemcontafinanceira = 15
          )
          AND (CAST(:vencimentoInicial AS date) IS NULL OR DATE(cf.datavencimento) >= CAST(:vencimentoInicial AS date))
          AND (CAST(:vencimentoFinal AS date) IS NULL OR DATE(cf.datavencimento) <= CAST(:vencimentoFinal AS date))
          AND (:empresaId IS NULL OR cf.idtenant = :empresaId)
          AND (
            :status IS NULL 
            OR (
                :status = 'A' AND cf.pago = FALSE
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.tipohistorico NOT IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                -- Excluir contas que são consideradas pagas por outras regras
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                AND NOT (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            )
            OR (:status = 'B' AND (
                cf.pago = TRUE 
                OR UPPER(cf.destinocontafinanceira) = 'P'
                OR cf.tipohistorico IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                OR (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                OR (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                OR (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            ))
            OR (:status = 'P' AND cf.pago = FALSE 
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.datavencimento >= CURRENT_DATE
                AND cf.tipohistorico NOT IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                AND NOT (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            )
            OR (:status = 'V' AND cf.pago = FALSE 
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.datavencimento < CURRENT_DATE
                AND cf.tipohistorico NOT IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                AND NOT (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            )
          )
    """, nativeQuery = true)
    Long countContasPorClienteComFiltros(
        @Param("idCliente") Long idCliente,
        @Param("vencimentoInicial") LocalDate vencimentoInicial,
        @Param("vencimentoFinal") LocalDate vencimentoFinal,
        @Param("status") String status,
        @Param("empresaId") Long empresaId
    );
    
    /**
     * Busca contas por cliente com filtros e paginação
     */
    @Query(value = """
        SELECT cf.* 
        FROM contafinanceira cf 
        LEFT JOIN meiopagamento mp ON cf.idmeiopagamento = mp.idmeiopagamento
        LEFT JOIN contrato ct ON cf.idcontrato = ct.idcontrato
        INNER JOIN tipoorigemcontafinanceira toc ON cf.idorigemconta = toc.idtipoorigemcontafinanceira
        WHERE (
            -- Buscar por idpessoa direto na conta financeira (pode ser NULL)
            cf.idpessoa = :idCliente
            -- OU buscar por cessionário do contrato
            OR ct.idpessoacessionario = :idCliente
            -- OU buscar por co-cessionário do contrato
            OR ct.idpessoacocessionario = :idCliente
          )
          AND cf.tipohistorico NOT IN ('RENEGOCIADA', 'EXCLUIDO', 'CANCELADO')
          AND (
            toc.sysid IN ('ENTRADA', 'INTERMEDIARIA', 'MULTARESCIS', 'PARC', 'REEMBOLSO')
            OR toc.idtipoorigemcontafinanceira = 15
          )
          AND (CAST(:vencimentoInicial AS date) IS NULL OR DATE(cf.datavencimento) >= CAST(:vencimentoInicial AS date))
          AND (CAST(:vencimentoFinal AS date) IS NULL OR DATE(cf.datavencimento) <= CAST(:vencimentoFinal AS date))
          AND (:empresaId IS NULL OR cf.idtenant = :empresaId)
          AND (
            :status IS NULL 
            OR (
                :status = 'A' AND cf.pago = FALSE
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.tipohistorico NOT IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                -- Excluir contas que são consideradas pagas por outras regras
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                AND NOT (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            )
            OR (:status = 'B' AND (
                cf.pago = TRUE 
                OR UPPER(cf.destinocontafinanceira) = 'P'
                OR cf.tipohistorico IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                OR (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                OR (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                OR (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            ))
            OR (:status = 'P' AND cf.pago = FALSE 
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.datavencimento >= CURRENT_DATE
                AND cf.tipohistorico NOT IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                AND NOT (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            )
            OR (:status = 'V' AND cf.pago = FALSE 
                AND UPPER(COALESCE(cf.destinocontafinanceira, '')) <> 'P'
                AND cf.datavencimento < CURRENT_DATE
                AND cf.tipohistorico NOT IN ('BAIXADO', 'TRANSFERIDO', 'BAIXADOCARTACREDITO')
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = FALSE)
                AND NOT (mp.codmeiopagamento = 'CARTAORECORRENTE' AND cf.recorrenciaautorizada = TRUE)
                AND NOT (mp.codmeiopagamento = 'CARTAO' AND COALESCE(mp.utilizadoparalinkpagamento, FALSE) = TRUE AND cf.recorrenciaautorizada = TRUE)
            )
          )
        ORDER BY cf.datavencimento ASC
        LIMIT :limite OFFSET :offset
    """, nativeQuery = true)
    List<ContaFinanceira> findContasPorClienteComFiltrosPaginado(
        @Param("idCliente") Long idCliente,
        @Param("vencimentoInicial") LocalDate vencimentoInicial,
        @Param("vencimentoFinal") LocalDate vencimentoFinal,
        @Param("status") String status,
        @Param("empresaId") Long empresaId,
        @Param("limite") Integer limite,
        @Param("offset") Integer offset
    );
    
    /**
     * Obtém o último número de parcela para um contrato e origem específicos
     */
    @Query("SELECT MAX(cf.numeroParcela) FROM ContaFinanceira cf WHERE cf.contrato.id = :idContrato AND cf.origemConta.idTipoOrigemContaFinanceira = :idOrigemConta AND cf.tipoHistorico = 'ATIVO'")
    Integer obterUltimoNroParcelaContratoOrigem(@Param("idContrato") Long idContrato, @Param("idOrigemConta") Integer idOrigemConta);
}