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
     * Verifica se existe tag ativa vinculada a grupos específicos para um contrato
     * 
     * @param idContrato ID do contrato
     * @param idsGrupoTag IDs dos grupos (idconvencaosistema) que bloqueiam
     * @return true se existir tag ativa com os critérios especificados
     */
    @Query("""
        SELECT CASE WHEN COUNT(ct) > 0 THEN true ELSE false END
        FROM ContratoTag ct
        JOIN ct.tipoTag tt
        WHERE ct.contrato.id = :idContrato
          AND ct.ativo = true
          AND EXISTS (
            SELECT 1 FROM ContratoTipoTagGrupoTag cttgt 
            WHERE cttgt.tipoTag.id = tt.id
              AND cttgt.deletado = false
              AND cttgt.grupoTag.id IN :idsGrupoTag
          )
    """)
    boolean existsTagAtivaVincudaAGrupos(
        @Param("idContrato") Long idContrato, 
        @Param("idsGrupoTag") List<Long> idsGrupoTag
    );

    /**
     * Busca as descrições das tags ativas vinculadas a grupos específicos para um contrato
     * 
     * @param idContrato ID do contrato
     * @param idsGrupoTag IDs dos grupos (idconvencaosistema) que bloqueiam
     * @return Lista com as descrições das tags encontradas
     */
    @Query("""
        SELECT DISTINCT tt.descricao
        FROM ContratoTag ct
        JOIN ct.tipoTag tt
        WHERE ct.contrato.id = :idContrato
          AND ct.ativo = true
          AND EXISTS (
            SELECT 1 FROM ContratoTipoTagGrupoTag cttgt 
            WHERE cttgt.tipoTag.id = tt.id
              AND cttgt.deletado = false
              AND cttgt.grupoTag.id IN :idsGrupoTag
          )
    """)
    List<String> findTagsBloqueantes(
        @Param("idContrato") Long idContrato, 
        @Param("idsGrupoTag") List<Long> idsGrupoTag
    );
}

