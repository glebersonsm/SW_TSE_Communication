package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.ContratoIntercambio;

@Repository
public interface ContratoIntercambioRepository extends JpaRepository<ContratoIntercambio, Long> {
    
    /**
     * Busca contrato de intercâmbio ativo por ID do contrato
     */
    @Query("SELECT ci FROM ContratoIntercambio ci WHERE ci.contrato.id = :idContrato AND ci.tipoHistorico = 'ATIVO'")
    Optional<ContratoIntercambio> findByContratoIdAndTipoHistoricoAtivo(@Param("idContrato") Long idContrato);
}

