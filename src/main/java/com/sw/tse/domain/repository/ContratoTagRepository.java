package com.sw.tse.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ContratoTag;

@Repository
public interface ContratoTagRepository extends JpaRepository<ContratoTag, Long> {
    
    /**
     * Verifica se existe tag ativa com grupo específico para um contrato
     * 
     * @param idContrato ID do contrato
     * @param sysId SysId da ConvencaoSistema para identificar o grupo
     * @return true se existir tag ativa com o grupo especificado
     */
    @Query("""
        SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END
        FROM ContratoTag ct
        JOIN ct.tipoTag tt
        JOIN ContratoTipoTagGrupoTag cttgt ON cttgt.tipoTag.id = tt.id
        JOIN cttgt.grupoTag cs
        WHERE ct.contrato.id = :idContrato
          AND ct.ativo = true
          AND cs.sysId = :sysId
          AND cttgt.deletado = false
    """)
    boolean existsTagAtivaComGrupo(@Param("idContrato") Long idContrato, @Param("sysId") String sysId);
    
    /**
     * Verifica se existe tag ativa por tipos de tag ou grupos para um contrato
     * 
     * @param idContrato ID do contrato
     * @param idsTipoTag IDs dos tipos de tag que bloqueiam (opcional)
     * @param sysIdsGrupo SysIds dos grupos que bloqueiam (opcional)
     * @return true se existir tag ativa com os critérios especificados
     */
    @Query("""
        SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END
        FROM ContratoTag ct
        WHERE ct.contrato.id = :idContrato
          AND ct.ativo = true
          AND (
            (:idsTipoTag IS NULL OR ct.tipoTag.id IN :idsTipoTag)
            OR 
            EXISTS (
              SELECT 1 FROM ContratoTipoTagGrupoTag cttgt 
              JOIN cttgt.grupoTag cs
              WHERE cttgt.tipoTag.id = ct.tipoTag.id
                AND cttgt.deletado = false
                AND (:sysIdsGrupo IS NULL OR cs.sysId IN :sysIdsGrupo)
            )
          )
    """)
    boolean existsTagAtivaPorTiposOuGrupos(
        @Param("idContrato") Long idContrato, 
        @Param("idsTipoTag") List<Long> idsTipoTag, 
        @Param("sysIdsGrupo") List<String> sysIdsGrupo
    );
}
