package com.sw.tse.domain.service.impl.db;

import org.springframework.stereotype.Service;

import com.sw.tse.domain.repository.ContratoTagRepository;
import com.sw.tse.domain.service.interfaces.ContratoTagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContratoTagServiceImpl implements ContratoTagService {
    
    private final ContratoTagRepository contratoTagRepository;
    
    private static final String SYS_ID_BLOQUEIO_RESERVA = "CONTRATOTS_TIPOTAG_GRUPOTAG_PENDENTEUTILIZACAO";
    
    @Override
    public boolean contratoTemBloqueioReserva(Long idContrato) {
        log.debug("Verificando bloqueio de reserva para contrato ID: {}", idContrato);
        
        boolean temBloqueio = contratoTagRepository.existsTagAtivaComGrupo(idContrato, SYS_ID_BLOQUEIO_RESERVA);
        
        if (temBloqueio) {
            log.info("Contrato ID {} possui bloqueio ativo de reserva", idContrato);
        } else {
            log.debug("Contrato ID {} não possui bloqueio ativo de reserva", idContrato);
        }
        
        return temBloqueio;
    }
}
