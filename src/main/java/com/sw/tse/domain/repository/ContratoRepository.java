package com.sw.tse.domain.repository;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponseRaw;
import com.sw.tse.domain.model.db.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    @Query("SELECT c FROM Contrato c " +
           "LEFT JOIN c.pessoaCessionario pc " +
           "LEFT JOIN c.pessaoCocessionario pco " +
           "WHERE pc.cpfCnpj = :cpf " +
           "OR pco.cpfCnpj = :cpf " +
           "ORDER BY c.id DESC")
    List<Contrato> findByPessoaCpf(String cpf);

    @Query(value = """
        SELECT 
            ct.idcontrato AS idContrato,
            COALESCE(ct.numerocontrato, '') AS numeroContrato,
            COALESCE(pro.descricao, '') AS descricaoProduto,
            CAST(COALESCE(ct.qtdpontoscontratados, 0) AS DECIMAL(19,2)) AS qtdPontosContratados,
            CAST(0 AS DECIMAL(19,2)) AS percentualMinimoIntegralizadoExigido,
            CAST(0 AS DECIMAL(19,2)) AS qtdPontosDebitados,
            CAST(0 AS DECIMAL(19,2)) AS qtdPontosUtilizados,
            CAST(0 AS DECIMAL(19,2)) AS qtdPontosCompraAvulsa,
            CAST(0 AS DECIMAL(19,2)) AS qtdPontosDebitarPorNaoUtilizacao,
            CAST(0 AS DECIMAL(19,2)) AS qtdPontosDebitadosPorNaoUtilizacao,
            CAST(0 AS DECIMAL(19,2)) AS qtdTotalPontosDebitadosPorNaoUtilizacao,
            ct.datacadastro AS dataVenda,
            NULL AS dataEfetivacaoDebitoPorNaoUtilizacao,
            NULL AS dataEfetivarDebitoPorNaoUtilizacao,
            CAST(COALESCE(ct.valornegociado, 0) AS DECIMAL(19,2)) AS valorNegociado,
            CAST(COALESCE(financeiro.valorEntrada, 0) AS DECIMAL(19,2)) AS valorTotalEntrada,
            CAST(COALESCE(financeiro.valorSaldo, 0) AS DECIMAL(19,2)) AS valorTotalSaldoRestante,
            CAST(COALESCE(
                CASE WHEN COALESCE(financeiro.valorPago, 0) = 0 OR COALESCE(ct.valornegociado, 0) = 0 THEN 0 
                ELSE ROUND(financeiro.valorPago / ct.valornegociado * 100, 2) 
                END, 0
            ) AS DECIMAL(19,2)) AS porcentagemIntegralizadaSobreValorNegociado,
            COALESCE(ps.razaosocial, '') AS nomeCessionario,
            CAST(0 AS DECIMAL(19,2)) AS qtdPontosLiberadosParaUso,
            CAST(COALESCE(financeiro.qtdeEmAtrazo, 0) AS INTEGER) AS qtdParcelasVencidas,
            CAST(COALESCE(financeiro.QtdDiasVencido, 0) AS INTEGER) AS qtdDiasVencido,
            proximaUtilizacao.ProximaUtilizacao AS proximaUtilizacao,
            CAST(0 AS BIGINT) AS idGrupoTabelaPontos,
            CAST(COALESCE(ct.idtenant, 0) AS BIGINT) AS idEmpresa,
            ct.datainicio AS dataInicioVigencia,
            ct.datatermino AS dataFimVigencia,
            CAST(COALESCE(financeiro.valorpago, 0) AS DECIMAL(19,2)) AS valorTotalIntegralizado,
            CAST(COALESCE(financeiro.valorEmAtrazo, 0) AS DECIMAL(19,2)) AS valorTotalEmAtraso,
            CAST(0 AS DECIMAL(19,2)) AS porcentagemAIntegralizarSobreValorNegociado,
            CASE 
                WHEN ct.idcotaadquirida IS NOT NULL THEN 'COTAS'
                ELSE 'PONTOS'
            END AS tipoContrato,
            COALESCE(ct.statuscontrato, 'ATIVO') AS statusContrato,
            CAST(0 AS DECIMAL(19,2)) AS saldoPontosGeral,
            CAST(COALESCE(financeiro.valorBrutoPago, 0) AS DECIMAL(19,2)) AS valorBrutoRecebido,
            CAST(0 AS DECIMAL(19,2)) AS valorReembolsoPago
        FROM contrato ct
        LEFT JOIN pessoa ps ON ps.idpessoa = ct.idpessoacessionario
        INNER JOIN produto pro ON pro.idproduto = ct.idproduto
        LEFT JOIN (
            SELECT 
                cf.idcontrato,
                SUM(CASE WHEN toc.sysid = 'ENTRADA' THEN cf.valorparcela END) AS valorEntrada,
                SUM(CASE WHEN toc.sysid = 'PARC' THEN cf.valorparcela END) AS valorSaldo,
                SUM(CASE WHEN cf.tipohistorico IN('BAIXADO','TRANSFERIDO','BAIXADOCARTACREDITO') THEN cf.valorparcela END) AS valorpago,
                SUM(CASE WHEN (cf.tipohistorico IN('BAIXADO','TRANSFERIDO','BAIXADOCARTACREDITO') OR (cf.tipohistorico ='ATIVO' AND cf.recorrenciaautorizada = TRUE) OR 
                    (mp.codmeiopagamento ='CARTAO' AND mp.utilizadoparalinkpagamento = FALSE AND cf.tipohistorico ='ATIVO')) THEN cf.valorrecebido + cf.descontotaxacartao END) AS valorBrutoPago,
                SUM(CASE WHEN cf.datavencimento < CURRENT_DATE AND cf.tipohistorico = 'ATIVO' AND 
                    (cf.recorrenciaautorizada = FALSE OR cf.recorrenciaautorizada IS NULL) THEN cf.valorparcela END) AS valorEmAtrazo,
                COUNT(CASE WHEN cf.datavencimento < CURRENT_DATE AND cf.tipohistorico = 'ATIVO' AND 
                    (cf.recorrenciaautorizada = FALSE OR cf.recorrenciaautorizada IS NULL) 
                    AND (mp.codmeiopagamento != 'CARTAO' AND mp.utilizadoparalinkpagamento = FALSE) THEN cf.idcontafinanceira END) AS qtdeEmAtrazo,
                (CURRENT_DATE - MIN(CASE WHEN cf.datavencimento < CURRENT_DATE AND cf.tipohistorico = 'ATIVO' AND 
                    (cf.recorrenciaautorizada = FALSE OR cf.recorrenciaautorizada IS NULL) 
                    AND (mp.codmeiopagamento != 'CARTAO' AND mp.utilizadoparalinkpagamento = FALSE) THEN cf.datavencimento END)::DATE) AS QtdDiasVencido
            FROM contafinanceira cf
            LEFT JOIN meiopagamento mp ON mp.idmeiopagamento = cf.idmeiopagamento
            LEFT JOIN tipoorigemcontafinanceira toc ON toc.idtipoorigemcontafinanceira = cf.idorigemconta
            WHERE toc.sysid IN('ENTRADA','PARC','INTERMEDIARIA') 
                AND cf.tipohistorico IN ('ATIVO', 'BAIXADO','TRANSFERIDO', 'BAIXADOCARTACREDITO') 
                AND cf.destinocontafinanceira ='R'
            GROUP BY cf.idcontrato
        ) financeiro ON financeiro.idcontrato = ct.idcontrato
        LEFT JOIN (
            SELECT 
                uc.idcontrato,
                MIN(uc.DataCheckIn) AS ProximaUtilizacao
            FROM utilizacaocontrato uc
            INNER JOIN tipoutilizacaocontrato tuc ON tuc.idtipoutilizacaocontrato = uc.idtipoutilizacaocontrato
            WHERE uc.DataCheckIn::DATE > NOW()::DATE 
                AND uc.DataCheckIn > '0001-01-01' 
                AND uc.status IN('ATIVO','CONFIRMADO') 
                AND tuc.sigla IN('RESERVA')
            GROUP BY uc.idContrato
        ) proximaUtilizacao ON proximaUtilizacao.idcontrato = ct.idcontrato
        WHERE (ct.idpessoacessionario = :idPessoaCliente OR ct.idpessoacocessionario = :idPessoaCliente)
        """, nativeQuery = true)
    List<ContratoClienteApiResponseRaw> buscarContratosClientePorIdPessoaRaw(@Param("idPessoaCliente") Long idPessoaCliente);

    default List<ContratoClienteApiResponse> buscarContratosClientePorIdPessoa(Long idPessoaCliente) {
        return buscarContratosClientePorIdPessoaRaw(idPessoaCliente).stream()
                .map(this::convertToApiResponse)
                .collect(Collectors.toList());
    }

    default ContratoClienteApiResponse convertToApiResponse(ContratoClienteApiResponseRaw raw) {
        return new ContratoClienteApiResponse(
                raw.getIdContrato(),
                raw.getNumeroContrato(),
                raw.getDescricaoProduto(),
                raw.getQtdPontosContratados(),
                raw.getPercentualMinimoIntegralizadoExigido(),
                raw.getQtdPontosDebitados(),
                raw.getQtdPontosUtilizados(),
                raw.getQtdPontosCompraAvulsa(),
                raw.getQtdPontosDebitarPorNaoUtilizacao(),
                raw.getQtdPontosDebitadosPorNaoUtilizacao(),
                raw.getQtdTotalPontosDebitadosPorNaoUtilizacao(),
                raw.getDataVenda() != null ? raw.getDataVenda().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null,
                raw.getDataEfetivacaoDebitoPorNaoUtilizacao() != null ? raw.getDataEfetivacaoDebitoPorNaoUtilizacao().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null,
                raw.getDataEfetivarDebitoPorNaoUtilizacao() != null ? raw.getDataEfetivarDebitoPorNaoUtilizacao().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null,
                raw.getValorNegociado(),
                raw.getValorTotalEntrada(),
                raw.getValorTotalSaldoRestante(),
                raw.getPorcentagemIntegralizadaSobreValorNegociado(),
                raw.getNomeCessionario(),
                raw.getQtdPontosLiberadosParaUso(),
                raw.getQtdParcelasVencidas(),
                raw.getQtdDiasVencido(),
                raw.getProximaUtilizacao() != null ? raw.getProximaUtilizacao().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null,
                raw.getIdGrupoTabelaPontos(),
                raw.getIdEmpresa(),
                raw.getDataInicioVigencia() != null ? raw.getDataInicioVigencia().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null,
                raw.getDataFimVigencia() != null ? raw.getDataFimVigencia().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime() : null,
                raw.getValorTotalIntegralizado(),
                raw.getValorTotalEmAtraso(),
                raw.getPorcentagemAIntegralizarSobreValorNegociado(),
                raw.getTipoContrato(),
                raw.getStatusContrato(),
                raw.getSaldoPontosGeral(),
                raw.getValorBrutoRecebido(),
                raw.getValorReembolsoPago()
        );
    }

    /**
     * Verifica se o contrato pertence ao cliente (cessionário ou cocessionário)
     * 
     * @param idContrato ID do contrato a ser verificado
     * @param idPessoaCliente ID da pessoa do cliente autenticado
     * @return true se o contrato pertence ao cliente, false caso contrário
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contrato c " +
           "WHERE c.id = :idContrato " +
           "AND (c.pessoaCessionario.idPessoa = :idPessoaCliente OR c.pessaoCocessionario.idPessoa = :idPessoaCliente)")
    boolean contratoPerteceAoCliente(@Param("idContrato") Long idContrato, @Param("idPessoaCliente") Long idPessoaCliente);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contrato c " +
           "WHERE (c.pessoaCessionario.idPessoa = :idPessoa OR c.pessaoCocessionario.idPessoa = :idPessoa)")
    boolean pessoaEhProprietariaDeAlgumContrato(@Param("idPessoa") Long idPessoa);

    @Query("SELECT DISTINCT new com.sw.tse.api.model.EmpresaTseDto(e.id, e.sigla) " +
           "FROM Contrato c " +
           "JOIN c.empresa e " +
           "WHERE c.pessoaCessionario.idPessoa = :idPessoa " +
           "AND (c.status = 'ATIVO' OR c.status = 'ATIVOREV') " +
           "ORDER BY e.sigla")
    List<com.sw.tse.api.model.EmpresaTseDto> findEmpresasByPessoaComContratosAtivos(@Param("idPessoa") Long idPessoa);

}
