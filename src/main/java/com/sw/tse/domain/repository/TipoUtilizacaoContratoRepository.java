package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.TipoUtilizacaoContrato;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoUtilizacaoContratoRepository extends JpaRepository<TipoUtilizacaoContrato, Long> {

    /**
     * Busca tipo de utilização de contrato por sigla
     */
    Optional<TipoUtilizacaoContrato> findBySigla(String sigla);

    /**
     * Busca tipo de utilização de contrato por descrição
     */
    Optional<TipoUtilizacaoContrato> findByDescricao(String descricao);

    /**
     * Lista tipos de utilização por sigla (busca parcial)
     */
    @Query("SELECT t FROM TipoUtilizacaoContrato t WHERE LOWER(t.sigla) LIKE LOWER(CONCAT('%', :sigla, '%')) ORDER BY t.sigla")
    List<TipoUtilizacaoContrato> findBySiglaContainingIgnoreCase(@Param("sigla") String sigla);

    /**
     * Lista tipos de utilização por descrição (busca parcial)
     */
    @Query("SELECT t FROM TipoUtilizacaoContrato t WHERE LOWER(t.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')) ORDER BY t.descricao")
    List<TipoUtilizacaoContrato> findByDescricaoContainingIgnoreCase(@Param("descricao") String descricao);

    /**
     * Verifica se existe tipo de utilização com a sigla informada
     */
    boolean existsBySigla(String sigla);

    /**
     * Verifica se existe tipo de utilização com a descrição informada
     */
    boolean existsByDescricao(String descricao);

    /**
     * Lista todos os tipos de utilização ordenados por sigla
     */
    @Query("SELECT t FROM TipoUtilizacaoContrato t ORDER BY t.sigla")
    List<TipoUtilizacaoContrato> findAllOrderBySigla();

    /**
     * Lista todos os tipos de utilização ordenados por descrição
     */
    @Query("SELECT t FROM TipoUtilizacaoContrato t ORDER BY t.descricao")
    List<TipoUtilizacaoContrato> findAllOrderByDescricao();
}
