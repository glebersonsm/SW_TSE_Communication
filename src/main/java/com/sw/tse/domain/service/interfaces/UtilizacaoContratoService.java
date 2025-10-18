package com.sw.tse.domain.service.interfaces;

import java.util.List;

import com.sw.tse.api.dto.HospedeReservaDto;
import com.sw.tse.domain.model.db.OperadorSistema;
import com.sw.tse.domain.model.db.PeriodoModeloCota;
import com.sw.tse.domain.model.db.TipoUtilizacaoContrato;
import com.sw.tse.domain.model.db.UtilizacaoContrato;

public interface UtilizacaoContratoService {
    
    /**
     * Cria uma nova utilização de contrato do tipo RESERVA
     * 
     * @param periodoModeloCota Período modelo cota
     * @param usuarioResponsavel Usuário responsável pela criação
     * @param tipoUtilizacaoContrato Tipo de utilização (deve ter sigla 'RESERVA')
     * @param hospedes Lista de hóspedes (obrigatório ao menos 1)
     * @return UtilizacaoContrato criada
     * @throws HospedesObrigatoriosException se lista de hóspedes estiver vazia
     * @throws TipoUtilizacaoContratoInvalidoException se tipo não for RESERVA
     * @throws PessoaNotFoundException se alguma pessoa não for encontrada
     * @throws TipoHospedeNotFoundException se algum tipo de hóspede não for encontrado
     * @throws FaixaEtariaNotFoundException se alguma faixa etária não for encontrada
     */
    UtilizacaoContrato criarUtilizacaoContratoReserva(
            PeriodoModeloCota periodoModeloCota,
            OperadorSistema usuarioResponsavel,
            TipoUtilizacaoContrato tipoUtilizacaoContrato,
            List<HospedeReservaDto> hospedes);
}

