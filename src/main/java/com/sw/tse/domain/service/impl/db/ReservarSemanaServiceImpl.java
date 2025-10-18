package com.sw.tse.domain.service.impl.db;

import org.springframework.stereotype.Service;

import com.sw.tse.domain.expection.ContratoNaoPertenceAoClienteException;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.service.interfaces.ContratoDisponibilidadeService;
import com.sw.tse.domain.service.interfaces.PeriodoDisponibilidadeService;
import com.sw.tse.domain.service.interfaces.ReservarSemanaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservarSemanaServiceImpl implements ReservarSemanaService {
    
    private final ContratoRepository contratoRepository;
    private final ContratoDisponibilidadeService contratoDisponibilidadeService;
    private final PeriodoDisponibilidadeService periodoDisponibilidadeService;
    
    @Override
    public void validarReserva(Long idContrato, Long idPeriodoUtilizacao, Long idPessoaCliente) {
        
        log.info("Iniciando validação de reserva - Contrato: {}, Período: {}, Cliente: {}", 
            idContrato, idPeriodoUtilizacao, idPessoaCliente);
        
        // 1. Validar se contrato pertence ao cliente
        log.debug("Validando se contrato {} pertence ao cliente {}", idContrato, idPessoaCliente);
        boolean contratoPerteceAoCliente = contratoRepository.contratoPerteceAoCliente(
            idContrato, 
            idPessoaCliente
        );
        
        if (!contratoPerteceAoCliente) {
            log.warn("Tentativa de reserva negada: Contrato {} não pertence ao cliente {}", 
                idContrato, idPessoaCliente);
            throw new ContratoNaoPertenceAoClienteException(
                String.format("O contrato %d não pertence ao cliente autenticado", idContrato)
            );
        }
        
        log.info("Contrato {} validado como pertencente ao cliente {}", idContrato, idPessoaCliente);
        
        // 2. Validar disponibilidade do contrato (inadimplência, integralização, tags)
        log.debug("Validando disponibilidade do contrato {}", idContrato);
        contratoDisponibilidadeService.validarDisponibilidadeParaReserva(idContrato);
        
        // 3. Validar disponibilidade do período específico (dupla checagem)
        log.debug("Validando disponibilidade do período {}", idPeriodoUtilizacao);
        periodoDisponibilidadeService.validarPeriodoDisponivel(idContrato, idPeriodoUtilizacao);
        
        log.info("Todas as validações passaram - Reserva pode ser criada");
    }
}

