package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.UtilizacaoContratoHospede;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilizacaoContratoHospedeRepository extends JpaRepository<UtilizacaoContratoHospede, Long> {

    /**
     * Lista hóspedes por utilização de contrato
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.utilizacaoContrato.id = :idUtilizacaoContrato ORDER BY h.isPrincipal DESC, h.nome")
    List<UtilizacaoContratoHospede> findByUtilizacaoContrato(@Param("idUtilizacaoContrato") Long idUtilizacaoContrato);

    /**
     * Busca hóspede principal da utilização de contrato
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.utilizacaoContrato.id = :idUtilizacaoContrato AND h.isPrincipal = true")
    Optional<UtilizacaoContratoHospede> findHospedePrincipalByUtilizacaoContrato(@Param("idUtilizacaoContrato") Long idUtilizacaoContrato);

    /**
     * Lista hóspedes por CPF
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.cpf = :cpf ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByCpf(@Param("cpf") String cpf);

    /**
     * Lista hóspedes por pessoa
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.pessoa.id = :idPessoa ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByPessoa(@Param("idPessoa") Long idPessoa);

    /**
     * Lista hóspedes por faixa etária
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.faixaEtaria.id = :idFaixaEtaria ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByFaixaEtaria(@Param("idFaixaEtaria") Long idFaixaEtaria);

    /**
     * Lista hóspedes por tipo de hóspede
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.tipoHospede.id = :idTipoHospede ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByTipoHospede(@Param("idTipoHospede") Long idTipoHospede);

    /**
     * Lista hóspedes por empresa
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.empresa.id = :idEmpresa ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByEmpresa(@Param("idEmpresa") Long idEmpresa);

    /**
     * Busca hóspede por nome (busca parcial)
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE LOWER(h.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY h.nome")
    List<UtilizacaoContratoHospede> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    /**
     * Busca hóspede por nome completo (nome + sobrenome)
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE LOWER(CONCAT(h.nome, ' ', h.sobrenome)) LIKE LOWER(CONCAT('%', :nomeCompleto, '%')) ORDER BY h.nome")
    List<UtilizacaoContratoHospede> findByNomeCompletoContainingIgnoreCase(@Param("nomeCompleto") String nomeCompleto);

    /**
     * Lista hóspedes principais
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.isPrincipal = true ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findHospedesPrincipais();

    /**
     * Lista hóspedes não principais
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.isPrincipal = false ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findHospedesNaoPrincipais();

    /**
     * Lista hóspedes por período de check-in
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.dataCheckin BETWEEN :dataInicio AND :dataFim ORDER BY h.dataCheckin")
    List<UtilizacaoContratoHospede> findByPeriodoCheckin(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Lista hóspedes por período de cadastro
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.dataCadastro BETWEEN :dataInicio AND :dataFim ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByPeriodoCadastro(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Conta hóspedes por utilização de contrato
     */
    @Query("SELECT COUNT(h) FROM UtilizacaoContratoHospede h WHERE h.utilizacaoContrato.id = :idUtilizacaoContrato")
    long countByUtilizacaoContrato(@Param("idUtilizacaoContrato") Long idUtilizacaoContrato);

    /**
     * Conta hóspedes principais por utilização de contrato
     */
    @Query("SELECT COUNT(h) FROM UtilizacaoContratoHospede h WHERE h.utilizacaoContrato.id = :idUtilizacaoContrato AND h.isPrincipal = true")
    long countHospedesPrincipaisByUtilizacaoContrato(@Param("idUtilizacaoContrato") Long idUtilizacaoContrato);

    /**
     * Verifica se existe hóspede com o CPF informado
     */
    boolean existsByCpf(String cpf);

    /**
     * Lista hóspedes com integração HotBeach incompleta
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.pessoaIncompletaIntegracaoHotbeach = true ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findComIntegracaoHotbeachIncompleta();

    /**
     * Lista hóspedes por múltiplas faixas etárias
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.faixaEtaria.id IN :idsFaixaEtaria ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByFaixaEtariaIn(@Param("idsFaixaEtaria") List<Long> idsFaixaEtaria);

    /**
     * Lista hóspedes por múltiplos tipos de hóspede
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.tipoHospede.id IN :idsTipoHospede ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByTipoHospedeIn(@Param("idsTipoHospede") List<Long> idsTipoHospede);

    /**
     * Lista hóspedes por empresa e faixa etária
     */
    @Query("SELECT h FROM UtilizacaoContratoHospede h WHERE h.empresa.id = :idEmpresa AND h.faixaEtaria.id = :idFaixaEtaria ORDER BY h.dataCadastro DESC")
    List<UtilizacaoContratoHospede> findByEmpresaAndFaixaEtaria(@Param("idEmpresa") Long idEmpresa, @Param("idFaixaEtaria") Long idFaixaEtaria);
}
