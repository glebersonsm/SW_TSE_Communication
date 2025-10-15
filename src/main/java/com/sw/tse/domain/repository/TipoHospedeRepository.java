package com.sw.tse.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.TipoHospede;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoHospedeRepository extends JpaRepository<TipoHospede, Long> {

    /**
     * Busca tipo de hóspede por descrição
     */
    Optional<TipoHospede> findByDescricao(String descricao);

    /**
     * Lista tipos de hóspede por empresa
     */
    @Query("SELECT t FROM TipoHospede t WHERE t.empresa.id = :idEmpresa ORDER BY t.descricao")
    List<TipoHospede> findByEmpresa(@Param("idEmpresa") Long idEmpresa);

    /**
     * Lista tipos de hóspede por descrição (busca parcial)
     */
    @Query("SELECT t FROM TipoHospede t WHERE LOWER(t.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')) ORDER BY t.descricao")
    List<TipoHospede> findByDescricaoContainingIgnoreCase(@Param("descricao") String descricao);

    /**
     * Busca tipo de hóspede por ID de integração
     */
    Optional<TipoHospede> findByIdIntegracao(String idIntegracao);

    /**
     * Verifica se existe tipo de hóspede com a descrição informada
     */
    boolean existsByDescricao(String descricao);

    /**
     * Verifica se existe tipo de hóspede com o ID de integração informado
     */
    boolean existsByIdIntegracao(String idIntegracao);

    /**
     * Lista tipos de hóspede por empresa e descrição
     */
    @Query("SELECT t FROM TipoHospede t WHERE t.empresa.id = :idEmpresa AND LOWER(t.descricao) LIKE LOWER(CONCAT('%', :descricao, '%')) ORDER BY t.descricao")
    List<TipoHospede> findByEmpresaAndDescricaoContainingIgnoreCase(@Param("idEmpresa") Long idEmpresa, @Param("descricao") String descricao);
}
