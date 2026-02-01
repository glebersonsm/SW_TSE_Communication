package com.sw.tse.domain.service.interfaces;

import com.sw.tse.domain.expection.ContratoBloqueadoPorTagException;
import com.sw.tse.domain.expection.ContratoInadimplenteException;
import com.sw.tse.domain.expection.ContratoIntegralizacaoInsuficienteException;
import com.sw.tse.domain.expection.ContratoNotFoundException;
import com.sw.tse.domain.expection.TipoValidacaoIntegralizacaoInvalidoException;
import com.sw.tse.domain.model.dto.ValidacaoDisponibilidadeParametros;

/**
 * Service para validação de disponibilidade de contrato para reserva
 */
public interface ContratoDisponibilidadeService {
    
    /**
     * Valida se um contrato está disponível para reserva usando configuração global
     * 
     * @param idContrato ID do contrato a ser validado
     * @throws ContratoBloqueadoPorTagException se contrato tiver tag de bloqueio ativa
     * @throws ContratoIntegralizacaoInsuficienteException se integralização for insuficiente
     * @throws ContratoInadimplenteException se contrato estiver inadimplente
     */
    void validarDisponibilidadeParaReserva(Long idContrato) 
            throws ContratoBloqueadoPorTagException, 
                   ContratoIntegralizacaoInsuficienteException, 
                   ContratoInadimplenteException;
    
    /**
     * Valida se um contrato está disponível para reserva com parâmetros customizados
     * 
     * @param idContrato ID do contrato a ser validado
     * @param parametros Parâmetros de validação (tags, integralização, inadimplência)
     * @throws ContratoBloqueadoPorTagException se contrato tiver tag de bloqueio ativa
     * @throws ContratoIntegralizacaoInsuficienteException se integralização for insuficiente
     * @throws ContratoInadimplenteException se contrato estiver inadimplente
     * @throws TipoValidacaoIntegralizacaoInvalidoException se tipo de validação de integralização for inválido
     * @throws ContratoNotFoundException se contrato não for encontrado
     */
    void validarDisponibilidadeParaReserva(
        Long idContrato, 
        ValidacaoDisponibilidadeParametros parametros
    ) throws ContratoBloqueadoPorTagException, 
             ContratoIntegralizacaoInsuficienteException, 
             ContratoInadimplenteException,
             TipoValidacaoIntegralizacaoInvalidoException,
             ContratoNotFoundException;
}
