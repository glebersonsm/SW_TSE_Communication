package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.api.dto.ReservaResumoResponse;
import com.sw.tse.api.dto.ReservaSemanaResponse;
import com.sw.tse.api.dto.ReservarSemanaRequest;
import com.sw.tse.domain.expection.ContratoBloqueadoPorTagException;
import com.sw.tse.domain.expection.ContratoInadimplenteException;
import com.sw.tse.domain.expection.ContratoIntegralizacaoInsuficienteException;
import com.sw.tse.domain.expection.ContratoNaoPertenceAoClienteException;
import com.sw.tse.domain.expection.PeriodoNaoDisponivelException;
import com.sw.tse.domain.expection.UtilizacaoContratoNotFoundException;

/**
 * Service para validação e criação de reservas de semanas
 */
public interface ReservarSemanaService {
    
    /**
     * Valida se uma reserva pode ser criada
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
    
    /**
     * Cria uma reserva completa (PeriodoModeloCota + UtilizacaoContrato)
     * 
     * @param request Dados da reserva
     * @param idPessoaCliente ID do cliente autenticado
     * @return Dados da reserva criada
     */
    ReservaSemanaResponse criarReserva(ReservarSemanaRequest request, Long idPessoaCliente);
    
    /**
     * Busca uma utilização de contrato existente
     * 
     * @param idUtilizacaoContrato ID da utilização
     * @param idPessoaCliente ID do cliente autenticado
     * @return Dados da utilização
     * @throws UtilizacaoContratoNotFoundException se utilização não existe
     * @throws ContratoNaoPertenceAoClienteException se contrato não pertence ao cliente
     */
    ReservaSemanaResponse buscarUtilizacao(Long idUtilizacaoContrato, Long idPessoaCliente);
    
    /**
     * Lista todas as utilizações não canceladas de um ano específico para o cliente
     * 
     * @param ano Ano das utilizações
     * @param idPessoaCliente ID do cliente autenticado
     * @return Lista de utilizações resumidas ordenadas por check-in
     */
    List<ReservaResumoResponse> listarReservasPorAno(int ano, Long idPessoaCliente);
    List<ReservaResumoResponse> listarReservasPorContratoEAno(Long idContrato, int ano, Long idPessoaCliente);
}

