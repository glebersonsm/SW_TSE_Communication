package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.domain.model.dto.PeriodoUtilizacaoDisponivel;

public interface PeriodoUtilizacaoService {

    /**
     * Busca períodos de utilização disponíveis para reserva baseado no contrato
     * 
     * @param idContrato ID do contrato
     * @param ano Ano para filtrar os períodos (opcional - se null, busca todos os anos)
     * @return Lista de períodos disponíveis com informações completas
     */
    List<PeriodoUtilizacaoDisponivel> buscarPeriodosDisponiveisParaReserva(Long idContrato, Integer ano);
}
