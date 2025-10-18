package com.sw.tse.domain.service.impl.db;

import org.springframework.stereotype.Service;

import com.sw.tse.domain.expection.PeriodoNaoDisponivelException;
import com.sw.tse.domain.repository.PeriodoUtilizacaoCustomRepository;
import com.sw.tse.domain.service.interfaces.PeriodoDisponibilidadeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PeriodoDisponibilidadeServiceImpl implements PeriodoDisponibilidadeService {
    
    private final PeriodoUtilizacaoCustomRepository customRepository;
    
    @Override
    public void validarPeriodoDisponivel(Long idContrato, Long idPeriodoUtilizacao) 
            throws PeriodoNaoDisponivelException {
        
        log.info("Validando disponibilidade do período {} para o contrato {}", 
            idPeriodoUtilizacao, idContrato);
        
        boolean disponivel = customRepository.verificarPeriodoDisponivel(
            idContrato,
            idPeriodoUtilizacao
        );
        
        if (!disponivel) {
            log.warn("Período {} não está disponível para o contrato {}", 
                idPeriodoUtilizacao, idContrato);
            throw new PeriodoNaoDisponivelException(idContrato, idPeriodoUtilizacao);
        }
        
        log.info("Período {} validado com sucesso para o contrato {}", 
            idPeriodoUtilizacao, idContrato);
    }
}

