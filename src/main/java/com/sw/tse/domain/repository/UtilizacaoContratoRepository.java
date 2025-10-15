package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.UtilizacaoContrato;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UtilizacaoContratoRepository extends JpaRepository<UtilizacaoContrato, Long> {

    /**
     * Busca utilização de contrato por GUID
     */
    Optional<UtilizacaoContrato> findByUtilizacaoContratoGuid(UUID guid);

    /**
     * Busca utilização de contrato por número da reserva
     */
    Optional<UtilizacaoContrato> findByNroReserva(Long nroReserva);

    /**
     * Lista utilizações por contrato
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.contrato.id = :idContrato ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByContrato(@Param("idContrato") Long idContrato);

    /**
     * Lista utilizações por empresa
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.empresa.id = :idEmpresa ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByEmpresa(@Param("idEmpresa") Long idEmpresa);

    /**
     * Lista utilizações por pessoa solicitante
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.pessoaSolicitante.id = :idPessoa ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByPessoaSolicitante(@Param("idPessoa") Long idPessoa);

    /**
     * Lista utilizações por status
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.status = :status ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByStatus(@Param("status") String status);

    /**
     * Lista utilizações ativas
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.status = 'ATIVO' ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findAtivas();

    /**
     * Lista utilizações confirmadas
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.utilizacaoConfirmada = true ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findConfirmadas();

    /**
     * Lista utilizações canceladas
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.status = 'CANCELADO' ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findCanceladas();

    /**
     * Lista utilizações por período de datas
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.dataCheckin BETWEEN :dataInicio AND :dataFim ORDER BY u.dataCheckin")
    List<UtilizacaoContrato> findByPeriodoCheckin(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Lista utilizações por período de cadastro
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.dataCadastro BETWEEN :dataInicio AND :dataFim ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByPeriodoCadastro(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Lista utilizações por unidade hoteleira
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.unidadeHoteleira.id = :idUnidadeHoteleira ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByUnidadeHoteleira(@Param("idUnidadeHoteleira") Long idUnidadeHoteleira);

    /**
     * Lista utilizações por tipo de utilização de contrato
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.tipoUtilizacaoContrato.id = :idTipoUtilizacaoContrato ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByTipoUtilizacaoContrato(@Param("idTipoUtilizacaoContrato") Long idTipoUtilizacaoContrato);

    /**
     * Lista utilizações por período modelo cota
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.periodoModeloCota.id = :idPeriodoModeloCota ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByPeriodoModeloCota(@Param("idPeriodoModeloCota") Long idPeriodoModeloCota);

    /**
     * Conta utilizações por contrato
     */
    @Query("SELECT COUNT(u) FROM UtilizacaoContrato u WHERE u.contrato.id = :idContrato")
    long countByContrato(@Param("idContrato") Long idContrato);

    /**
     * Conta utilizações ativas por contrato
     */
    @Query("SELECT COUNT(u) FROM UtilizacaoContrato u WHERE u.contrato.id = :idContrato AND u.status = 'ATIVO'")
    long countAtivasByContrato(@Param("idContrato") Long idContrato);

    /**
     * Verifica se existe utilização com o número da reserva informado
     */
    boolean existsByNroReserva(Long nroReserva);

    /**
     * Verifica se existe utilização com o GUID informado
     */
    boolean existsByUtilizacaoContratoGuid(UUID guid);

    /**
     * Lista utilizações por múltiplos status
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.status IN :status ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByStatusIn(@Param("status") List<String> status);

    /**
     * Lista utilizações por empresa e status
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.empresa.id = :idEmpresa AND u.status = :status ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findByEmpresaAndStatus(@Param("idEmpresa") Long idEmpresa, @Param("status") String status);

    /**
     * Lista utilizações enviadas para Capere
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.enviadoCapere = true ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findEnviadasCapere();

    /**
     * Lista utilizações confirmadas no Capere
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.confirmadoCapere = true ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findConfirmadasCapere();

    /**
     * Lista utilizações canceladas no Capere
     */
    @Query("SELECT u FROM UtilizacaoContrato u WHERE u.canceladoCapere = true ORDER BY u.dataCadastro DESC")
    List<UtilizacaoContrato> findCanceladasCapere();
}
