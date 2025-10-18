package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.expection.PeriodoNaoDisponivelException;

/**
 * Service para validação de disponibilidade de período para reserva
 */
public interface PeriodoDisponibilidadeService {
    
    /**
     * Valida se um período está disponível para reserva (dupla checagem)
     * 
     * @param idContrato ID do contrato
     * @param idPeriodoUtilizacao ID do período de utilização a validar
     * @throws PeriodoNaoDisponivelException se o período não estiver disponível
     */
    void validarPeriodoDisponivel(Long idContrato, Long idPeriodoUtilizacao) 
            throws PeriodoNaoDisponivelException;
}

