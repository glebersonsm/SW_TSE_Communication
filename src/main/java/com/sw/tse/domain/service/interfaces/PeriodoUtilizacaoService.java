package com.sw.tse.domain.service.interfaces;

import java.math.BigDecimal;
import java.util.List;

import com.sw.tse.domain.model.dto.PeriodoUtilizacaoDisponivel;

public interface PeriodoUtilizacaoService {

    /**
     * Busca períodos de utilização disponíveis para reserva baseado no contrato.
     * Os parâmetros de integralização (tipo e valor) são opcionais e vêm da Portal API (config INTEGRALIZACAO_CONTRATO_CONFIG).
     *
     * @param idContrato ID do contrato
     * @param ano Ano para filtrar os períodos (opcional - se null, busca todos os anos)
     * @param tipoValidacaoIntegralizacao "FIXO" ou "PERCENTUAL" (opcional)
     * @param valorIntegralizacao valor fixo em reais ou percentual (opcional)
     * @return Lista de períodos disponíveis com informações completas
     */
    List<PeriodoUtilizacaoDisponivel> buscarPeriodosDisponiveisParaReserva(Long idContrato, Integer ano,
            String tipoValidacaoIntegralizacao, BigDecimal valorIntegralizacao);
}
