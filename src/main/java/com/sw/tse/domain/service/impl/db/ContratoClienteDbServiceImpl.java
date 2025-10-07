package com.sw.tse.domain.service.impl.db;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.sw.tse.domain.model.api.dto.ContratoClienteApiResponse;
import com.sw.tse.domain.repository.ContratoRepository;
import com.sw.tse.domain.repository.PessoaRepository;
import com.sw.tse.domain.service.interfaces.ContratoClienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@Service
@Slf4j
@RequiredArgsConstructor
public class ContratoClienteDbServiceImpl implements ContratoClienteService {

    private final ContratoRepository contratoRepository;
    private final PessoaRepository pessoaRepository;

    @Override
    public List<ContratoClienteApiResponse> buscarContratosPorToken(String token) {
        log.warn("Método buscarContratosPorToken não implementado na versão DB - use buscarContratosPorIdUsuario");
        throw new UnsupportedOperationException("Busca por token não suportada na implementação DB. Use buscarContratosPorIdUsuario.");
    }

    @Override
    public List<ContratoClienteApiResponse> buscarContratosPorIdUsuario(Long idUsuario) {
    	throw new UnsupportedOperationException("Busca por token não suportada na implementação DB. Use buscarContratosPorIdUsuario.");
    }
}