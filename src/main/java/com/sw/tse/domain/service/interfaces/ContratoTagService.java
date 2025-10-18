package com.sw.tse.domain.service.interfaces;

/**
 * Service para gerenciar tags de contrato e verificar bloqueios
 */
public interface ContratoTagService {
    
    /**
     * Verifica se um contrato tem bloqueio ativo para reserva
     * 
     * @param idContrato ID do contrato a ser verificado
     * @return true se o contrato tem bloqueio ativo de reserva, false caso contrário
     */
    boolean contratoTemBloqueioReserva(Long idContrato);
}
