package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.expection.ContratoBloqueadoPorTagException;
import com.sw.tse.domain.expection.ContratoInadimplenteException;
import com.sw.tse.domain.expection.ContratoIntegralizacaoInsuficienteException;
import com.sw.tse.domain.expection.ContratoNaoPertenceAoClienteException;
import com.sw.tse.domain.expection.PeriodoNaoDisponivelException;

/**
 * Service para validação e criação de reservas de semanas
 */
public interface ReservarSemanaService {
    
    /**
     * Valida se uma reserva pode ser criada
     * (por enquanto só valida, não cria a reserva)
     * 
     * @param idContrato ID do contrato
     * @param idPeriodoUtilizacao ID do período a reservar
     * @param idPessoaCliente ID do cliente autenticado
     * @throws ContratoNaoPertenceAoClienteException se contrato não pertence ao cliente
     * @throws ContratoBloqueadoPorTagException se contrato tem tag de bloqueio
     * @throws ContratoIntegralizacaoInsuficienteException se integralização insuficiente
     * @throws ContratoInadimplenteException se contrato inadimplente
     * @throws PeriodoNaoDisponivelException se período não disponível
     */
    void validarReserva(Long idContrato, Long idPeriodoUtilizacao, Long idPessoaCliente);
}

