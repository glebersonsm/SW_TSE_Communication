package com.sw.tse.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ContaFinanceira;

@Repository
public interface ContaFinanceiraRepository extends JpaRepository<ContaFinanceira, Long> {

    List<ContaFinanceira> findByContratoId(Long idContrato);

    List<ContaFinanceira> findByPessoaIdPessoa(Long idPessoa);

    List<ContaFinanceira> findByPagoAndDataVencimentoBefore(Boolean pago, LocalDateTime data);

    List<ContaFinanceira> findByPago(Boolean pago);

    List<ContaFinanceira> findByTipoHistorico(String tipoHistorico);


    List<ContaFinanceira> findByDestinoContaFinanceira(String destinoContaFinanceira);

    @Query("SELECT cf FROM ContaFinanceira cf WHERE cf.pago = false AND cf.dataVencimento < :dataAtual")
    List<ContaFinanceira> findContasEmAtraso(@Param("dataAtual") LocalDateTime dataAtual);

    List<ContaFinanceira> findByEmpresaId(Long idEmpresa);

    @Query("SELECT cf FROM ContaFinanceira cf WHERE cf.dataVencimento BETWEEN :dataInicio AND :dataFim")
    List<ContaFinanceira> findByDataVencimentoBetween(@Param("dataInicio") LocalDateTime dataInicio, 
                                                     @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT cf FROM ContaFinanceira cf WHERE cf.dataPagamento BETWEEN :dataInicio AND :dataFim")
    List<ContaFinanceira> findByDataPagamentoBetween(@Param("dataInicio") LocalDateTime dataInicio, 
                                                    @Param("dataFim") LocalDateTime dataFim);


    @Query("SELECT COUNT(cf) FROM ContaFinanceira cf WHERE cf.pago = false")
    Long countContasNaoPagas();

    @Query("SELECT COALESCE(SUM(cf.valorReceber), 0) FROM ContaFinanceira cf WHERE cf.pago = false")
    Double sumValoresContasNaoPagas();


    @Query("""
        SELECT DISTINCT cf FROM ContaFinanceira cf 
        LEFT JOIN FETCH cf.contrato c 
        LEFT JOIN FETCH c.pessoaCessionario pc
        LEFT JOIN FETCH c.pessaoCocessionario pcc
        LEFT JOIN FETCH c.cotaUh cu
        LEFT JOIN FETCH cf.empresa e 
        LEFT JOIN FETCH cf.meioPagamento mp 
        LEFT JOIN FETCH cf.carteiraBoleto cb 
        LEFT JOIN FETCH cf.origemConta oc 
        WHERE (c.pessoaCessionario.idPessoa = :idCliente 
            OR c.pessaoCocessionario.idPessoa = :idCliente) 
        AND (cf.tipoHistorico IS NULL 
            OR cf.tipoHistorico NOT IN ('RENEGOCIADA', 'EXCLUIDO', 'CANCELADO'))
        ORDER BY c.id ASC, cf.dataVencimento ASC
        """)
    List<ContaFinanceira> findContasPorCliente(@Param("idCliente") Long idCliente);

}
