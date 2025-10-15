package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.FaixaEtaria;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaixaEtariaRepository extends JpaRepository<FaixaEtaria, Long> {

    /**
     * Busca faixa etária por sigla
     */
    Optional<FaixaEtaria> findBySigla(String sigla);

    /**
     * Lista todas as faixas etárias ativas
     */
    @Query("SELECT f FROM FaixaEtaria f WHERE f.ativo = true ORDER BY f.sigla")
    List<FaixaEtaria> findAtivas();

    /**
     * Lista faixas etárias por descrição (busca parcial)
     */
    @Query("SELECT f FROM FaixaEtaria f WHERE LOWER(f.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')) ORDER BY f.sigla")
    List<FaixaEtaria> findByDescricaoContainingIgnoreCase(@Param("descricao") String descricao);

    /**
     * Verifica se existe faixa etária com a sigla informada
     */
    boolean existsBySigla(String sigla);

    /**
     * Busca faixas etárias que são pagantes
     */
    @Query("SELECT f FROM FaixaEtaria f WHERE f.isPagante = true AND f.ativo = true ORDER BY f.sigla")
    List<FaixaEtaria> findPagantesAtivas();

    /**
     * Busca faixas etárias isentas de pontos
     */
    @Query("SELECT f FROM FaixaEtaria f WHERE f.isIsentoPontos = true AND f.ativo = true ORDER BY f.sigla")
    List<FaixaEtaria> findIsentasPontosAtivas();

    /**
     * Busca faixas etárias isentas de taxas
     */
    @Query("SELECT f FROM FaixaEtaria f WHERE f.isIsentoTaxas = true AND f.ativo = true ORDER BY f.sigla")
    List<FaixaEtaria> findIsentasTaxasAtivas();
}
