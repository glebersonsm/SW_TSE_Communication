package com.sw.tse.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sw.tse.domain.model.db.StatusFinanceiroCondominio;

@Repository
public interface StatusFinanceiroCondominioRepository extends JpaRepository<StatusFinanceiroCondominio, Long> {

    /**
     * Busca o status financeiro do condomínio pelo ID do contrato SPE (Origem ADM).
     * 
     * @param idContratoSpe ID do contrato principal da SPE.
     * @return Optional com o status financeiro se encontrado.
     */
    Optional<StatusFinanceiroCondominio> findByIdContratoSpe(Long idContratoSpe);

}
